package actors

import akka.actor._
import da.SocketPool
import play.api.libs.json._

object WebSocketActor {
  def props(socketPool: SocketPool, out: ActorRef): Props = Props(new WebSocketActor(socketPool, out))

}

class WebSocketActor (val socketPool: SocketPool, out: ActorRef) extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case rawMessage: String =>
      val command = Json.parse(rawMessage)
        .asOpt[Command]
        .getOrElse(throw new Exception("Command not recognized"))

      command.msgType match {
        case "create" =>
          self ! CreateGame()
        case "join" =>
          self ! command.data.asOpt[JoinGame].get
        case "players" =>
          self ! command.data.asOpt[GetPlayers].get
        case "send" =>
          self ! command.data.asOpt[SendMessage].get
      }
    case _: CreateGame =>
      val id = socketPool.createPool(out)
      out ! s"pool id $id"
    case msg: JoinGame =>
      val success = socketPool.addConnection(msg.id, out)
      out ! s"Attempting to join game... $success"
    case msg: GetPlayers =>
      val players = socketPool.getConnections(msg.id)
      out ! s"I received your message to get players $players"
    case msg: SendMessage =>
      val actors = socketPool.getConnections(msg.id)
      actors.map( a => {
        a ! msg.message
      })
    case _ =>
      out ! s"Unknown message..."
  }
}

case class CreateGame()

case class SendMessage(id: Long, message: String)
object SendMessage {
  implicit val format: OFormat[SendMessage] = Json.format[SendMessage]
}

case class GetPlayers(id: Long)
object GetPlayers {
  implicit val format: OFormat[GetPlayers] = Json.format[GetPlayers]
}

case class JoinGame(id: Long)
object JoinGame {
  implicit val format: OFormat[JoinGame] = Json.format[JoinGame]
}

case class Command(msgType: String, data: JsValue)
object Command {
  implicit val format: OFormat[Command] = Json.format[Command]
}

