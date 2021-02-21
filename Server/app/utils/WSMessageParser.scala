package utils

import models.rest.{InternalWSMessage, WSCreateGame, WSJoinGame, WSMessage, WSMessageType, WSBroadcastMessage}
import play.api.libs.json.JsValue

object WSMessageParser {
  def parse(json: JsValue): Option[InternalWSMessage] = {
    val message = json.asOpt[WSMessage] match {
      case Some(value) => value
      case _ => throw new Exception("Could not parse websocket message")
    }

    message.msgType match {
      case WSMessageType.CreateGame => Some(WSCreateGame())
      case WSMessageType.JoinGame => message.data.asOpt[WSJoinGame]
      case WSMessageType.BroadcastMessage => message.data.asOpt[WSBroadcastMessage]
      case _ => throw new Exception("Unknown WS Message Type")
    }
  }
}
