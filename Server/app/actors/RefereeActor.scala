package actors

import actors.PlayerActor.PlayerAdded
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

  final case class AddPlayer(player: ActorRef[PlayerActor],
                             replyTo: ActorRef[PlayerAdded]) extends Message

  final case class CreateGame(replyTo: ActorRef[String]) extends Message
}

class RefereeActor(id: String)(implicit context: ActorContext[RefereeActor.Message])
    extends AbstractBehavior[RefereeActor.Message](context) {
  import RefereeActor._

  implicit val timeout: Timeout = Timeout(50.millis)
  implicit val system: ActorSystem[Nothing] = context.system
  val log: Logger = context.log

  var playerList: scala.collection.mutable.Seq[ActorRef[PlayerActor]] = scala.collection.mutable.Seq()

  override def onMessage(msg: Message): Behavior[Message] = {
    msg match {
      case AddPlayer(player, replyTo) =>
        log.debug(s"Adding player $player")
        playerList :+ player

        replyTo ! PlayerActor.PlayerAdded(true)
        this
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Message]] = {
    case PostStop =>
      this
  }

}
