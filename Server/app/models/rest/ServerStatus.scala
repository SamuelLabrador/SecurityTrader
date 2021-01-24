package models.rest

import play.api.libs.json.Json
import java.time.ZonedDateTime

case class ServerStatus(time: Long,
                        address: String)

object ServerStatus {
  implicit val serverStatusFormat = Json.format[ServerStatus]
}
