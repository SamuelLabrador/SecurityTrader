package models.rest

import play.api.libs.json._

case class WebSocketMessage(msgType: String,
                            data: JsValue)
object WebSocketMessage {
  implicit val format: OFormat[WebSocketMessage] = Json.format[WebSocketMessage]
}