package actors

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, Behavior }
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

  final case class Create(id: String, replyTo: ActorRef[Flow[JsValue, JsValue, NotUsed]])

  @Provides def apply(childFactory: PlayerActor.Factory, configuration: Configuration)
                     (implicit ec: ExecutionContext): Behavior[Create] = {

    implicit val timeout = Timeout(2.seconds)

    Behaviors.setup { context =>
      Behaviors.logMessages {
        Behaviors.receiveMessage {
          case Create(id, replyTo) =>
            val child = context.spawn(childFactory(), id)
            Behaviors.same
        }
      }
    }
  }
}