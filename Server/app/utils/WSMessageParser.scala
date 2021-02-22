package utils

import models.rest.{InternalWSMessage, WSBroadcastMessage, WSCreateGame, WSJoinGame, WSMessage, WSMessageType, WSPing}
import play.api.Logger
import play.api.libs.json.JsValue

/**
 * This object is used to parse raw websocket messages and convert them
 * to the corresponding WebSocket messages defined in models.rest.WebSocket
 */
object WSMessageParser {

  val log = Logger(this.getClass)

  /**
   * Parses the raw json and attempts to convert it to an InternalWSMessage
   *
   * @param json The JsValue to be parsed
   * @return An option that may contain the new class from the parsed JSON
   */
  def parse(json: JsValue): Option[InternalWSMessage] = {

    json.asOpt[WSMessage] match {

      case Some(message) =>
        message.msgType match {
          case WSMessageType.CreateGame => Some(WSCreateGame())
          case WSMessageType.JoinGame => message.data.asOpt[WSJoinGame]
          case WSMessageType.BroadcastMessage => message.data.asOpt[WSBroadcastMessage]
          case WSMessageType.PingMessage => Some(WSPing())
          case _ =>
            log.error(s"Unknown WS Message Type: $message")
            None
        }

      case _ =>
        log.error("Could not parse websocket message")
        None
    }


  }
}
