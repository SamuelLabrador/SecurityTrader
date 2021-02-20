package actors

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.{Done, NotUsed}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, PostStop, Scheduler}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.pattern.pipe
import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink}
import akka.util.Timeout
import models.rest.WSSendMessage
import play.api.libs.json.JsValue
import org.slf4j.Logger
import utils.WSMessageParser

import java.time.ZonedDateTime
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import scala.util.Try

object PlayerActor {
  sealed trait Message

  private case object InternalStop extends Message

  trait Factory {
    def apply(id: String): Behavior[Message]
  }

  def apply(id: String)(implicit mat: Materializer, ec: ExecutionContext): Behavior[Message] = {
    Behaviors.setup { implicit context =>
      implicit val scheduler: Scheduler = context.system.scheduler
      new PlayerActor(id).behavior
    }
  }

  final case class BroadcastMessage(message: String) extends Message
  final case class Connect(replyTo: ActorRef[Flow[JsValue, JsValue, NotUsed]]) extends Message
  final case class PlayerAdded(success: Boolean) extends Message
  final case class CreateGame() extends Message
}

class PlayerActor (id: String) (implicit context: ActorContext[PlayerActor.Message],
                                implicit val scheduler: Scheduler) {
  import PlayerActor._

  implicit val timeout: Timeout             = Timeout(50.millis)
  implicit val system: ActorSystem[Nothing] = context.system
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
      case m: WSSendMessage => sendMessage(m)
      case _ => {
        log.error(s"Unknown message received $parsedMessage")
      }
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
      case PlayerAdded(success) =>
        log.info("A player has been added")
        Behaviors.same
      case BroadcastMessage(message) =>
        context.log.info(s"Sending message: $message")
        Behaviors.same
      case CreateGame() =>
        context.log.info(s"Creating game")
//        refereeParentActor.ask(replyTo => RefereeParentActor.Create(id, replyTo))

        Behaviors.same
      case InternalStop =>
        Behaviors.stopped
    }.receiveSignal {
      case (_, PostStop) =>
        log.info("Stopping actor {}", context.self)
        Behaviors.same
    }
  }

  def sendMessage(msg: WSSendMessage): Unit = {
    log.debug(s"Received $msg")
  }
}