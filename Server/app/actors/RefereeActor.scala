package actors

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, PostStop, Signal}
import akka.stream.Materializer
import akka.util.Timeout
import org.slf4j.Logger

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object RefereeActor {
  sealed trait Message

  trait Factory {
    def apply(id: String): Behavior[Message]
  }

  def apply(id: String)(implicit mat: Materializer, ec: ExecutionContext): Behavior[Message] = {
    Behaviors.setup { implicit context =>
      // Setup service key for future discovery
      val RefereeActorServiceKey = ServiceKey[Message](id)
      context.system.receptionist ! Receptionist.Register(RefereeActorServiceKey, context.self)
      new RefereeActor(id)
    }
  }

  /*
  * Receive Messages
  * */
  final case class JoinRequest(replyTo: ActorRef[PlayerActor.Message]) extends Message
  final case class BroadcastMessage(message: String) extends Message
}

/**
 * The RefereeActor manages the state of the game.
 * It tracks the players and rounds.
 *
 * @param id Unique ID to track the referee actor
 * @param context context for internal usage
 */
class RefereeActor(id: String)(implicit context: ActorContext[RefereeActor.Message])
    extends AbstractBehavior[RefereeActor.Message](context) {
  import RefereeActor._

  implicit val timeout: Timeout = Timeout(50.millis)
  implicit val system: ActorSystem[Nothing] = context.system
  val log: Logger = context.log


  var playerList: scala.collection.mutable.Seq[ActorRef[PlayerActor.Message]] =
    scala.collection.mutable.Seq.empty[ActorRef[PlayerActor.Message]]


  /**
   * Meat and potatoes of the RefereeActor. Primary logic.
   * Will handle interactions between player and the system.
   *
   * @param msg Message telling the referee actor what to do
   * @return
   */
  override def onMessage(msg: Message): Behavior[Message] = {
    msg match {

      case JoinRequest(replyTo) =>
        log.debug(s"Adding player $replyTo")
        playerList = playerList :+ replyTo
        replyTo ! PlayerActor.RefereeAssignment(context.self)
        this

      case BroadcastMessage(message) =>
        log.debug("Referee received broadcasting message")
        playerList.foreach( p => {
          log.debug(s"Sending message $message to $p")
          p ! PlayerActor.InboxMessage(message)
        })
        this
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Message]] = {
    case PostStop =>
      log.debug(s"Stopping referee actor ${context.self}")
      this
  }
}
