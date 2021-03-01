import javax.inject.{Inject, Provider, Singleton}

import actors._
import akka.stream.Materializer
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

import scala.concurrent.ExecutionContext

/**
 * This class handles handles the dependency injections
 */
class Module extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindTypedActor(GameEngineActor(), "gameEngineActor")
    bindTypedActor(PlayerParentActor, "userParentActor")
    bindTypedActor(RefereeParentActor, "refereeParentActor")
    bind(classOf[PlayerActor.Factory]).toProvider(classOf[PlayerActorFactoryProvider])
    bind(classOf[RefereeActor.Factory]).toProvider(classOf[RefereeActorFactoryProvider])
  }
}

@Singleton
class PlayerActorFactoryProvider @Inject()(mat: Materializer,
                                           ec: ExecutionContext
                                          ) extends Provider[PlayerActor.Factory] {
  def get() = PlayerActor(_, _)(mat, ec)
}

@Singleton
class RefereeActorFactoryProvider @Inject()(mat: Materializer,
                                            ec: ExecutionContext
                                           ) extends Provider[RefereeActor.Factory] {
  def get() = RefereeActor(_)(mat, ec)
}