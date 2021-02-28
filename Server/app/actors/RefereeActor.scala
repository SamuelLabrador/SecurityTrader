package actors


import scala.util.{Success, Failure}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, PostStop, Signal}
import akka.stream.Materializer
import akka.util.Timeout
import models.rest.GameState
import org.slf4j.Logger

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object RefereeActor {
  sealed trait Command

  trait Factory {
    def apply(id: String): Behavior[Command]
  }

  def apply(id: String)(implicit mat: Materializer, ec: ExecutionContext): Behavior[Command] = {
    Behaviors.setup { implicit context =>
      // Setup service key for future discovery
      val RefereeActorServiceKey = ServiceKey[Command](id)
      context.system.receptionist ! Receptionist.Register(RefereeActorServiceKey, context.self)
      new RefereeActor(id)
    }
  }

  /*
  * Receive Messages
  * */
  final case class JoinRequest(replyTo: ActorRef[PlayerActor.Command]) extends Command
  final case class BroadcastMessage(message: String) extends Command
  final case class GameStateUpdate(gameState: GameState)
  final case class AssignActor(ref: ActorRef[GameEngineActor.Command]) extends Command
  final case class CreateGame() extends Command
}

/**
 * The RefereeActor manages the state of the game.
 * It tracks the players and rounds.
 *
 * @param id Unique ID to track the referee actor
 * @param context context for internal usage
 */
class RefereeActor(id: String)(implicit context: ActorContext[RefereeActor.Command])
    extends AbstractBehavior[RefereeActor.Command](context) {
  import RefereeActor._

  implicit val timeout: Timeout = Timeout(50.millis)
  implicit val system: ActorSystem[Nothing] = context.system
  val log: Logger = context.log


  var playerList: scala.collection.mutable.Seq[ActorRef[PlayerActor.Command]] =
    scala.collection.mutable.Seq.empty[ActorRef[PlayerActor.Command]]

  var gameState: Option[GameState] = None

  var gameEngine: Option[ActorRef[GameEngineActor.Command]] = None

  /**
   * Ask receptionist for the GameEngine reference
   * */
  context.ask(context.system.receptionist, Receptionist.Find(ServiceKeyConstants.GameEngineServiceKey)) {
    case Success(msg: Receptionist.Listing) => {
      msg match {
        case ServiceKeyConstants.GameEngineServiceKey.Listing(listings) => {
          log.debug(s"received listings: $listings")
          val ref = listings.toVector(0)
          AssignActor(ref)
        }
        case _ => throw new Exception("Cannot get game engine")
      }
    }
    case Failure(exception) =>
      log.debug(s"Received $exception")
      throw new Exception("Did not get listing response")
  }

  /**
   * Meat and potatoes of the RefereeActor. Primary logic.
   * Will handle interactions between player and the system.
   *
   * @param msg Message telling the referee actor what to do
   * @return
   */
  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {

      case JoinRequest(replyTo) =>
        log.debug(s"Adding player $replyTo")
        playerList = playerList :+ replyTo
        replyTo ! PlayerActor.RefereeAssignment(context.self)
        this

      case BroadcastMessage(message) =>
        playerList.foreach( p => {
          log.trace(s"Sending message $message to $p")
          p ! PlayerActor.InboxMessage(message)
        })
        this

      case CreateGame() =>
        log.debug("Referee request for game to be created")
        this

      case AssignActor(gameEngine) =>
        log.debug("Game engine assigned")
        this.gameEngine = Some(gameEngine)
        this
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = {
    case PostStop =>
      log.debug(s"Stopping referee actor ${context.self}")
      this
  }
}
