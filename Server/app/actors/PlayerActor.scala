package actors

import akka.{Done, NotUsed}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, PostStop, Scheduler}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink}
import akka.util.Timeout
import play.api.libs.json.JsValue
import org.slf4j.Logger

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import scala.util.Try

class PlayerActor()(implicit context: ActorContext[PlayerActor.Message]) {
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
    print(json)
  }

  /**
   * Generates a flow that can be used by the websocket.
   *
   * @return the flow of JSON
   */
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
        replyTo ! websocketFlow
        Behaviors.same
      case SendMessage(message) =>
        context.log.info(s"Sending message: $message")
        Behaviors.same
      case InternalStop =>
        Behaviors.stopped
    }.receiveSignal {
      case (_, PostStop) =>
        log.info("Stopping actor {}", context.self)
        Behaviors.same
    }
  }
}

object PlayerActor {
  sealed trait Message

  private case object InternalStop extends Message

  trait Factory {
    def apply(): Behavior[Message]
  }

  def apply()(implicit mat: Materializer, ec: ExecutionContext): Behavior[Message] = {
    Behaviors.setup { implicit context =>
      implicit val scheduler: Scheduler = context.system.scheduler
      new PlayerActor().behavior
    }
  }

  final case class SendMessage(message: String) extends Message
  final case class Connect(replyTo: ActorRef[Flow[JsValue, JsValue, NotUsed]]) extends Message
}