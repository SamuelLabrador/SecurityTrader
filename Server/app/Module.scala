import actors._
import akka.stream.Materializer
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

import javax.inject.{Inject, Provider, Singleton}
import scala.concurrent.ExecutionContext

class Module extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindTypedActor(PlayerParentActor, "userParentActor")
    bind(classOf[PlayerActor.Factory]).toProvider(classOf[PlayerActorFactoryProvider])
  }
}

@Singleton
class PlayerActorFactoryProvider @Inject()(mat: Materializer,
                                           ec: ExecutionContext
                                          ) extends Provider[PlayerActor.Factory] {
  def get() = PlayerActor()(mat, ec)
}