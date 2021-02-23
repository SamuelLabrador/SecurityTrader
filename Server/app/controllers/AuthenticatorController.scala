package controllers

import da.{DatabaseInterface, SecurityTraderDatabase}
import models.rest.{AuthResponse, CreateRequest, LoginRequest}
import play.api.libs.json.{JsError, JsSuccess, Json, OFormat, Writes}
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}
import play.mvc.Results.status

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthenticatorController @Inject() (val dbI: DatabaseInterface,
                                         val controllerComponents: ControllerComponents) (implicit ec: ExecutionContext) extends BaseController{
    def createUser() = Action.async { implicit request: Request[AnyContent] =>
      val credentials = request.body.asJson.get.validate[CreateRequest] match {
        case JsSuccess(value, _) => value
        case e: JsError => throw new Exception(s"Could not parse query: ${JsError.toJson(e)}")
      }

      // Call database function to add new user in table
      val success: Future[Boolean] = dbI.createUser(credentials.email, credentials.userName, credentials.password)
      success.map( result => {
          val value = Json.toJson(AuthResponse(result))
          Ok(value)
      })
  }

  def userLogin() = Action.async { implicit request: Request[AnyContent] =>
      val credentials = request.body.asJson.get.validate[LoginRequest] match {
        case JsSuccess(value, _) => value
        case e: JsError => throw new Exception(s"Could not parse query: ${JsError.toJson(e)}")
      }

      val success: Future[Boolean] = dbI.checkPswd(credentials.email, credentials.password)
      success.map( result => {
        val value = Json.toJson(AuthResponse(result))
        Ok(value)
      })
  }
}

