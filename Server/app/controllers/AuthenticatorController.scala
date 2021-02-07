package controllers

import da.SecurityTraderDatabase
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json.{JsError, JsSuccess, Json, OFormat, Writes}
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}

import javax.inject.{Inject, Singleton}

@Singleton
class AuthenticatorController @Inject() (val db: SecurityTraderDatabase,
                                        val controllerComponents: ControllerComponents) extends BaseController{
    def createUser() = Action { implicit request: Request[AnyContent] =>
      val credentials = request.body.asJson.get.validate[CreateRequest] match {
        case JsSuccess(value, _) => value
        case e: JsError => throw new Exception(s"Could not parse query: ${JsError.toJson(e)}")
      }
      // Encrypt user password
      val ePass = BCrypt.hashpw(credentials.password, BCrypt.gensalt())

      // Call database function to add new user in table
      db.createUser(credentials.email, credentials.userName, ePass)
      Ok("User has been Created!")
  }
}

case class CreateRequest(email: String, userName: String, password: String){}
object CreateRequest{
  implicit val jsonWrites: Writes[CreateRequest] = Json.writes[CreateRequest]
  implicit val format: OFormat[CreateRequest] = Json.format[CreateRequest]
}