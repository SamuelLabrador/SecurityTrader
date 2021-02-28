package models.rest

import play.api.libs.json.Json

case class Player(id: String,
                  email: Option[String],
                  userName: String,
                  isMod: Boolean)

object Player { implicit val fmt = Json.format[Player]}
