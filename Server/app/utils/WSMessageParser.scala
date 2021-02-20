package utils

import models.rest.{InternalWSMessage, WSMessage, WSMessageType, WSSendMessage}
import play.api.libs.json.JsValue

object WSMessageParser {
  def parse(json: JsValue): Option[InternalWSMessage] = {
    val message = json.asOpt[WSMessage] match {
      case Some(value) => value
      case _ => throw new Exception("Could not parse websocket message")
    }

    message.msgType match {
      case WSMessageType.SendMessage => message.data.asOpt[WSSendMessage]
      case _ => throw new Exception("Unknown WS Message Type")
    }
  }
}
