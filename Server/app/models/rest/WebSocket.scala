package models.rest

import play.api.libs.json.{JsValue, Json, OFormat}

object WSMessageType {
  // To Server
  val CreateGame = "CreateGame"
  val JoinGame = "JoinGame"
  val BroadcastMessage = "BroadcastMessage"
  val PingMessage = "PingMessage"

  // To Client
  val InboxMessage = "InboxMessage"
  val PongMessage = "PongMessage"
}

// This class is exposed to the public.
case class WSMessage(msgType: String, data: JsValue)
object WSMessage { implicit val fmt: OFormat[WSMessage] = Json.format[WSMessage] }

abstract class WSData()

case class WSBroadcastMessage(message: String) extends WSData
object WSBroadcastMessage { implicit val fmt: OFormat[WSBroadcastMessage] = Json.format[WSBroadcastMessage] }

case class WSCreateGame() extends WSData

case class WSPing() extends WSData

case class WSJoinGame(id: String) extends WSData
object WSJoinGame { implicit val fmt: OFormat[WSJoinGame] = Json.format[WSJoinGame] }

case class WSInboxMessage(message: String) extends WSData
object WSInboxMessage { implicit val fmt: OFormat[WSInboxMessage] = Json.format[WSInboxMessage] }

case class WSPongMessage(success: Boolean) extends WSData
object WSPongMessage { implicit val fmt: OFormat[WSPongMessage] = Json.format[WSPongMessage] }
