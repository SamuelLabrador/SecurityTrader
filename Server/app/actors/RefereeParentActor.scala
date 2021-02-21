package actors

import actors.PlayerActor.RefereeAssignment
import actors.RefereeActor
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import com.google.inject.Provides
import play.api.Configuration
import play.api.libs.concurrent.ActorModule

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

/**
 * Manages the creation of referee actors
 */
object RefereeParentActor extends ActorModule {
  type Message = Create

  final case class Create(id: String, replyTo: ActorRef[PlayerActor.Message])

  @Provides def apply(childFactory: RefereeActor.Factory, configuration: Configuration)
                     (implicit ec: ExecutionContext): Behavior[Create] = {

    implicit val timeout: Timeout = Timeout(2.seconds)

    Behaviors.setup { context =>
      Behaviors.logMessages {
        Behaviors.receiveMessage {
          case Create(id, playerActor) =>
            val name = s"refereeActor-$id"
            val child = context.spawn(childFactory(id), name)
            context.log.debug(s"Referee actor created with name $name")
            child ! RefereeActor.AddPlayer(playerActor, playerActor)
            Behaviors.same
        }
      }
    }
  }
}
