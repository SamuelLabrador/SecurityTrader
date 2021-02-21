package actors

import akka.{Done, NotUsed}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, PostStop, Scheduler}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}
import akka.util.Timeout
import models.rest.{WSBroadcastMessage, WSCreateGame, WSInboxMessage, WSJoinGame, WSMessage, WSMessageType}
import play.api.libs.json.{JsValue, Json}
import org.slf4j.Logger
import utils.WSMessageParser

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import scala.util.Try

object PlayerActor {
  sealed trait Message

  private case object InternalStop extends Message

  trait Factory {
    def apply(id: String, refereeParentActor: ActorRef[RefereeParentActor.Message]): Behavior[Message]
  }

  def apply(id: String, refereeParentActor: ActorRef[RefereeParentActor.Message])
           (implicit mat: Materializer, ec: ExecutionContext): Behavior[Message] = {
    Behaviors.setup { implicit context =>
      implicit val scheduler: Scheduler = context.system.scheduler
      new PlayerActor(id, refereeParentActor).behavior
    }
  }

  final case class Connect(replyTo: ActorRef[Flow[JsValue, JsValue, NotUsed]]) extends Message
  final case class BroadcastMessage(message: String) extends Message
  final case class CreateGame() extends Message
  final case class RefereeAssignment(refereeActor: ActorRef[RefereeActor.Message]) extends Message
  final case class InboxMessage(message: String) extends Message
}

class PlayerActor (id: String, refereeParentActor: ActorRef[RefereeParentActor.Message])
                  (implicit context: ActorContext[PlayerActor.Message], implicit val scheduler: Scheduler) {
  import PlayerActor._

  implicit val timeout: Timeout             = Timeout(50.millis)
  implicit val system: ActorSystem[Nothing] = context.system

  var referee: Option[ActorRef[RefereeActor.Message]] = None

  val log: Logger = context.log

  val (hubSink, hubSource) = MergeHub.source[JsValue](perProducerBufferSize = 16)
    .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both)
    .run()

  // Process the input
  private val jsonSink: Sink[JsValue, Future[Done]] = Sink.foreach { json =>
    // When the user types in a stock in the upper right corner, this is triggered,
    log.debug(s"received json input $json")

    // Get WS Message
    val parsedMessage = WSMessageParser.parse(json) match {
      case Some(message) => message
      case _ => throw new Exception(s"Could not parse json $json")
    }

    // Match message
    parsedMessage match {
      case m: WSBroadcastMessage => sendMessage(m)
      case m: WSCreateGame => createGame(m)
      case m: WSJoinGame => joinGame(m)
      case _ =>
        log.error(s"Unknown message received $parsedMessage")
    }
  }

  private lazy val websocketFlow: Flow[JsValue, JsValue, NotUsed] = {
    // Put the source and sink together to make a flow of hub source as output (aggregating all
    // stocks as JSON to the browser) and the actor as the sink (receiving any JSON messages
    // from the browse), using a coupled sink and source.
    Flow.fromSinkAndSourceCoupled(jsonSink, hubSource).watchTermination() { (_, termination) =>
      // When the flow shuts down, make sure this actor also stops.
      context.pipeToSelf(termination)((_: Try[Done]) => InternalStop)
      NotUsed
    }
  }

  def behavior: Behavior[Message] = {
    Behaviors.receiveMessage[Message] {
      case Connect(replyTo) =>
        log.info("Establishing websocket connection")
        replyTo ! websocketFlow
        Behaviors.same

      case BroadcastMessage(message) =>
        log.info(s"Sending message: $message")

        if (referee.isDefined) {
          log.debug("Sending broadcast message to referee")
          referee.get ! RefereeActor.BroadcastMessage(message)
        } else
          log.debug("No referee defined!")

        Behaviors.same

      case InboxMessage(message) =>
        val data = Json.toJson(WSInboxMessage(message))
        val wsMessage = Json.toJson(WSMessage(WSMessageType.InboxMessage, data))
        val source = Source(Seq(wsMessage))
        source.runWith(hubSink)
        Behaviors.same

      case RefereeAssignment(referee) =>
        log.info(s"Player assigned to referee: $referee")
        this.referee = Some(referee)
        Behaviors.same

      case CreateGame() =>
        log.info(s"Creating game")
        refereeParentActor ! RefereeParentActor.Create(id, context.self)
        Behaviors.same

      case InternalStop =>
        Behaviors.stopped
    }.receiveSignal {
      case (_, PostStop) =>
        log.info("Stopping actor {}", context.self)
        Behaviors.same
    }
  }

  def sendMessage(msg: WSBroadcastMessage): Unit = {
    log.debug(s"Received $msg")
    context.self ! BroadcastMessage(msg.message)
  }

  def createGame(msg: WSCreateGame): Unit = {
    if (referee.isDefined) {
      log.debug("Referee is already defined, not creating a game")
    } else {
      context.self ! CreateGame()
    }
  }

  def joinGame(msg: WSJoinGame): Unit = {
    log.debug(s"Joining game with message: $msg")
  }
}