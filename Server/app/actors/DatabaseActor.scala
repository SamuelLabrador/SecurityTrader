package actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import da.DatabaseInterface
import models.rest.Player

object DatabaseActor {
  sealed trait Command

  final case class CreateMessage(player: Player, message: String) extends Command

  def apply(dbI: DatabaseInterface): Behavior[Command] = {
    Behaviors.setup { context =>
      val log = context.log
      Behaviors.receiveMessage {
        case CreateMessage(player, message) =>
          log.debug("Database actor received a message")
          Behaviors.same
        case _ =>
          Behaviors.same

      }

    }
  }
}
