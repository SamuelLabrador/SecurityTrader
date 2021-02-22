package actors

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.{Done, NotUsed}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, PostStop, Scheduler}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}
import akka.util.Timeout
import models.rest.{WSBroadcastMessage, WSCreateGame, WSInboxMessage, WSJoinGame, WSMessage, WSMessageType, WSPing, WSPongMessage}
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

  final case class Ping() extends Message
  final case class Connect(replyTo: ActorRef[Flow[JsValue, JsValue, NotUsed]]) extends Message
  final case class BroadcastMessage(message: String) extends Message
  final case class CreateGame() extends Message
  final case class RefereeAssignment(refereeActor: ActorRef[RefereeActor.Message]) extends Message
  final case class InboxMessage(message: String) extends Message
  final case class ListingResponse(listing: Receptionist.Listing) extends Message
}

/**
 * The player actor handles the player's WebSocket connection
 *
 * @param id unique id so this actor can be discovered
 * @param refereeParentActor referee parent actor. Allows for user to create games
 * @param context context of actor. for internal usage
 * @param scheduler system scheduler. Gives guidance for how to handle incoming messages
 */
class PlayerActor (id: String, refereeParentActor: ActorRef[RefereeParentActor.Message])
                  (implicit context: ActorContext[PlayerActor.Message], implicit val scheduler: Scheduler) {
  import PlayerActor._

  implicit val timeout: Timeout             = Timeout(50.millis)
  implicit val system: ActorSystem[Nothing] = context.system
  val log: Logger = context.log
  val listingResponseAdapter: ActorRef[Receptionist.Listing] =
    context.messageAdapter[Receptionist.Listing](ListingResponse)

  var referee: Option[ActorRef[RefereeActor.Message]] = None
  var refereeActorServiceKey: Option[ServiceKey[RefereeActor.Message]] = None

  /**
   * Processes raw input from the websocket connection and directs the input accordingly
   *
   *  |-----------|       |------------|        |------------|
   *  |    raw    |       |  jsonSink  |        |   class    |
   *  |   input   | ====> |            | ====>  |  function  |
   *  |-----------|       |------------|        |------------|
   */
  private val jsonSink: Sink[JsValue, Future[Done]] = Sink.foreach { json =>
    // When the user types in a stock in the upper right corner, this is triggered,
    log.debug(s"received json input $json")

    // Get WS Message
    WSMessageParser.parse(json) match {

      case Some(message) =>
        message match {
          case m: WSBroadcastMessage => broadcastMessage(m)
          case m: WSCreateGame => createGame(m)
          case m: WSJoinGame => joinGame(m)
          case m: WSPing => onPing(m)
          case _ =>
            log.error(s"Unknown message received $message")
        }

      case _ =>
        log.error(s"Could not parse json $json")
    }
  }


  /**
   * Meat and potatoes. Central logic. Processes messages.
   * Sends messages to and from the player (webapp) and the referee
   */
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
        // Messages sent to the webapp need to be converted to JSON format
        // and encapsulated in Source
        val data = Json.toJson(WSInboxMessage(message))
        val wsMessage = Json.toJson(WSMessage(WSMessageType.InboxMessage, data))
        val source = Source(Seq(wsMessage))

        // Send payload to client
        source.runWith(hubSink)
        Behaviors.same

      case RefereeAssignment(referee) =>
        log.info(s"Player assigned to referee: $referee")
        this.referee = Some(referee)
        Behaviors.same

      case CreateGame() =>
        log.info(s"Creating game")
        // Make a request to the referee parent actor to create a referee for the player
        refereeParentActor ! RefereeParentActor.Create(id, context.self)
        Behaviors.same

      case Ping() =>
        log.info(s"Received ping, sending pong")
        val data = Json.toJson(WSPongMessage(true))
        val wsMessage = Json.toJson(WSMessage(WSMessageType.PongMessage, data))
        val source = Source(Seq(wsMessage))
        source.runWith(hubSink)
        Behaviors.same

      case ListingResponse(listing) =>
        val key = this.refereeActorServiceKey
          .getOrElse(throw new Exception("Addressing listing response with no key"))
        if (listing.isForKey(key)) {
          log.debug("Found referee!")
          val result = listing.allServiceInstances(key)
          if (result.size > 1)
            log.error("More than one referee found!")
          else {
            result.head ! RefereeActor.JoinRequest(context.self)
          }
        }

        Behaviors.same

      case InternalStop =>
        Behaviors.stopped
    }.receiveSignal {
      case (_, PostStop) =>
        log.info("Stopping actor {}", context.self)
        Behaviors.same
    }
  }

  /**
  * Functions for handling messages from player
  */

  /**
   * Broadcasts a message to the other users in the game
   *
   * @param msg the parsed WSBroadcastMessage
   */
  def broadcastMessage(msg: WSBroadcastMessage): Unit = {
    log.debug(s"Received $msg")
    context.self ! BroadcastMessage(msg.message)
  }

  /**
   * Creates a game. Games are defined by the referee
   *
   * @param msg the parsed WSCreateGame message
   */
  def createGame(msg: WSCreateGame): Unit = {
    if (referee.isDefined) {
      log.debug("Referee is already defined, not creating a game")
    } else {
      context.self ! CreateGame()
    }
  }

  /**
   * Joins a game
   *
   * @param msg the parsed WSJoinGame message
   */
  def joinGame(msg: WSJoinGame): Unit = {
    log.debug(s"Joining game with message: $msg")

    // Construct service key
    this.refereeActorServiceKey = Some(ServiceKey[RefereeActor.Message](msg.id))
    context.system.receptionist ! Receptionist.Find(this.refereeActorServiceKey.get, listingResponseAdapter)
  }

  /**
   * Sends Ping. Used to keep WebSocket connection alive.
   *
   * @param msg the parsed WSPing message
   */
  def onPing(msg: WSPing): Unit = {
    context.self ! Ping()
  }

  /* WebSocket Flow Setup */
  val (hubSink, hubSource) = MergeHub.source[JsValue](perProducerBufferSize = 16)
    .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both)
    .run()

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
}