package actors

import akka.actor.typed.receptionist.ServiceKey

/**
 * This class is used to store constants for discovering other actors
 */
object ServiceKeyConstants {
  val GameEngineServiceKey = ServiceKey[GameEngineActor.Command]("GameEngine")
}
