package actors

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.slf4j.Logger

/**
 * This actor is responsible for processing game logic inputs.
 */
object GameEngineActor {
  sealed trait Command

  final case class CreateGame(replyTo: ActorRef[RefereeActor.Command]) extends Command

  def apply(): Behavior[Command] = {

    Behaviors.setup { implicit context =>
      val GameEngineServiceKey = ServiceKeyConstants.GameEngineServiceKey
      val log: Logger = context.log

      context.system.receptionist ! Receptionist.Register(GameEngineServiceKey, context.self)

      Behaviors.logMessages {
        Behaviors.receiveMessage {
          case _ =>
            log.debug("The game engine received a message!")
            Behaviors.same
        }
      }
    }
  }
}
