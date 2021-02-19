package actors

import actors.PlayerActor.SendMessage
import akka.actor._
import akka.actor.typed.{ActorRef, Behavior, PostStop, Signal}
import akka.actor.typed.SpawnProtocol.Command
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import models.rest.Player

case class AddPlayer(playerID: String)
case class BroadcastMessage(player: Player, message: String)
case class MakeMove()

//object Referee {
//  def apply(gameID: String): Behavior[Message] =
//    Behaviors.setup(context => new Referee(context, gameID))
//
//  trait Message
//  final case class BroadcastMessage(message: String)
//    extends Message
//
//  private final case class RemovePlayer(player: ActorRef[PlayerActor])
//}

//class Referee(context: ActorContext[Referee.Message], gameID: String)
//    extends AbstractBehavior[Referee.Message](context) {
//  import Referee._
//
//  context.log.info(s"Referee started for game $gameID")
//
//  // playerList
//  private var playerList = Map.empty[Player, ActorRef[PlayerActor.Message]]
//
//  // WebSocket connection list
//  //  private var playerConnections: scala.collection.mutable.Seq[ActorRef] =
//  //    scala.collection.mutable.Seq()
//
//  // Current game state
//  // private var gameState: String = ""
//
//  override def onMessage(msg: Message) =
//    msg match {
//      case broadcastMsg @ BroadcastMessage() =>
//        this
//    }
//
//  override def onSignal: PartialFunction[Signal, Behavior[Any]] = {
//    case PostStop =>
//      context.log.info(s"Game $gameID stopped")
//      this
//  }
//}
