package actors

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, PostStop, Signal}
import akka.stream.Materializer
import akka.util.Timeout
import org.slf4j.Logger

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object RefereeActor {
  sealed trait Message

  private case object InternalStop extends Message

  trait Factory {
    def apply(id: String): Behavior[Message]
  }

  def apply(id: String)(implicit mat: Materializer, ec: ExecutionContext): Behavior[Message] = {
    Behaviors.setup { implicit context =>
      new RefereeActor(id)
    }
  }

  /*
  * Receive Messages
  * */
  final case class AddPlayer(player: ActorRef[PlayerActor.Message],
                             replyTo: ActorRef[PlayerActor.Message]) extends Message
  final case class BroadcastMessage(message: String) extends Message
}

class RefereeActor(id: String)(implicit context: ActorContext[RefereeActor.Message])
    extends AbstractBehavior[RefereeActor.Message](context) {
  import RefereeActor._

  implicit val timeout: Timeout = Timeout(50.millis)
  implicit val system: ActorSystem[Nothing] = context.system
  val log: Logger = context.log

  var playerList: scala.collection.mutable.Seq[ActorRef[PlayerActor.Message]] =
    scala.collection.mutable.Seq.empty[ActorRef[PlayerActor.Message]]

  override def onMessage(msg: Message): Behavior[Message] = {
    msg match {
      case AddPlayer(player, replyTo) =>
        log.debug(s"Adding player $player")
        playerList = playerList :+ player
        replyTo ! PlayerActor.RefereeAssignment(context.self)
        this

      case BroadcastMessage(message) =>
        log.debug("Referee received broadcasting message")
        log.debug(s"Player list $playerList")
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
