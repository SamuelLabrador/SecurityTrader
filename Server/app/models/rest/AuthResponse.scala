package models.rest

import play.api.libs.json.{Json, OFormat, Reads, Writes}

case class AuthResponse(success: Boolean){

}

object AuthResponse {
  implicit val createResponseFormat = Json.format[ServerStatus]
  implicit val jsonWrites: Writes[AuthResponse] = Json.writes[AuthResponse]
}

case class CreateRequest(email: String, userName: String, password: String){}
object CreateRequest{
  implicit val jsonWrites: Writes[CreateRequest] = Json.writes[CreateRequest]
  implicit val format: OFormat[CreateRequest] = Json.format[CreateRequest]
  implicit val reads: Reads[CreateRequest] = Json.reads[CreateRequest]
}

case class LoginRequest(email: String, password: String){}
object LoginRequest{
  implicit val jsonWrites: Writes[LoginRequest] = Json.writes[LoginRequest]
  implicit val format: OFormat[LoginRequest] = Json.format[LoginRequest]
  implicit val reads: Reads[LoginRequest] = Json.reads[LoginRequest]
}