package actors

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import com.google.inject.Provides
import play.api.Configuration
import play.api.libs.concurrent.ActorModule
import play.api.libs.json.JsValue

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
 * Provide some DI and configuration sugar for new UserActor instances.
 */
object PlayerParentActor extends ActorModule {
  type Message = Create

  sealed trait Command
  final case class Create(id: String, refereeParentActor: ActorRef[RefereeParentActor.Message],
                          replyTo: ActorRef[Flow[JsValue, JsValue, NotUsed]]) extends Command

  @Provides def apply(childFactory: PlayerActor.Factory, configuration: Configuration)
                     (implicit ec: ExecutionContext): Behavior[Create] = {

    implicit val timeout: Timeout = Timeout(2.seconds)

    Behaviors.setup { context =>
      Behaviors.logMessages {
        Behaviors.receiveMessage {
          case Create(id, refereeParentActor, replyTo) =>
            val name = s"playerActor-$id"
            val child = context.spawn(childFactory(id, refereeParentActor), name)
            child ! PlayerActor.Connect(replyTo)
            Behaviors.same
        }
      }
    }
  }
}