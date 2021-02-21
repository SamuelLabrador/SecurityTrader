package models.rest

import play.api.libs.json.{JsObject, JsValue, Json, OFormat, OWrites, Reads}

object WSMessageType {
  // To Server
  val CreateGame = "CreateGame"
  val JoinGame = "JoinGame"
  val BroadcastMessage = "BroadcastMessage"

  // To Client
  val InboxMessage = "InboxMessage"
}

// This class is exposed to the public.
case class WSMessage(msgType: String, data: JsValue)
object WSMessage { implicit val fmt: OFormat[WSMessage] = Json.format[WSMessage] }

// This class is only for internal use. Should not be exposed to public.
abstract class InternalWSMessage()

case class WSBroadcastMessage(message: String) extends InternalWSMessage
object WSBroadcastMessage { implicit val fmt: OFormat[WSBroadcastMessage] = Json.format[WSBroadcastMessage] }

case class WSCreateGame() extends InternalWSMessage

case class WSJoinGame(id: String) extends InternalWSMessage
object WSJoinGame { implicit val fmt: OFormat[WSJoinGame] = Json.format[WSJoinGame] }

case class WSInboxMessage(message: String) extends InternalWSMessage
object WSInboxMessage { implicit val fmt: OFormat[WSInboxMessage] = Json.format[WSInboxMessage] }

