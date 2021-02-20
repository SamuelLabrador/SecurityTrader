package models.rest

import play.api.libs.json.{JsValue, Json, OFormat}

object WSMessageType {
  val SendMessage = "SendMessage"
}

// This class is exposed to the public.
case class WSMessage(msgType: String, data: JsValue)
object WSMessage { implicit val fmt: OFormat[WSMessage] = Json.format[WSMessage] }

// This class is only for internal use. Should not be exposed to public.
abstract class InternalWSMessage()

case class WSSendMessage(message: String) extends InternalWSMessage
object WSSendMessage { implicit val fmt: OFormat[WSSendMessage] = Json.format[WSSendMessage] }
