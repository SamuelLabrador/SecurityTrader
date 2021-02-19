package models.rest

import play.api.libs.json.{Json, OFormat}

case class WebSocketMessageType ()
object WebSocketMessageType extends Enumeration {
  val SendMessage = Value("SendMessage")

//  implicit val fmt: OFormat[WebSocketMessageType] = Json.format[WebSocketMessageType]
}
//
//trait WSMessage
//
//case class SendMessage(message: String) extends WSMessage
//object SendMessage {
//  implicit val fmt = Json.format[SendMessage]
//}
//
//case class WebSocketMessage (msgType: WebSocketMessageType.Value, data: WSMessage)
//object WebSocketMessage {
//  implicit val fmt = Json.format[WebSocketMessage]
//}
