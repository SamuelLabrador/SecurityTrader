package controllers

import da.{DatabaseInterface, SecurityTraderDatabase}
import models.rest.{AuthResponse, CreateRequest, LoginRequest, Player}
import play.api.Configuration
import play.api.libs.json.{JsError, JsSuccess, Json, OFormat, Writes}
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}
import play.mvc.Results.status
import utils.TokenUtility

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthenticatorController @Inject() (val dbI: DatabaseInterface,
                                         val controllerComponents: ControllerComponents,
                                         val configuration: Configuration)
                                        (implicit ec: ExecutionContext) extends BaseController{

  val secret = configuration.underlying.getString("play.http.secret.key")

  def createUser() = Action.async { implicit request: Request[AnyContent] =>
    val credentials = request.body.asJson.get.validate[CreateRequest] match {
      case JsSuccess(value, _) => value
      case e: JsError => throw new Exception(s"Could not parse query: ${JsError.toJson(e)}")
    }

    // Call database function to add new user in table and get token
    val maybeToken = for {
      creationResult <- dbI.createUser(credentials.email, credentials.username, credentials.password)
      token <- if (creationResult) {
        this.generateToken(credentials.email)
      } else {
        Future.successful(None)
      }
    } yield {
      token
    }

    maybeToken.map( result => {
      result match {
        case Some(token) =>
          Ok(Json.toJson(AuthResponse(token)))

        case None => Unauthorized("Invalid username or password")
      }
    })
  }

  def userLogin() = Action.async { implicit request: Request[AnyContent] =>
    val credentials = request.body.asJson.get.validate[LoginRequest] match {
      case JsSuccess(value, _) => value
      case e: JsError => throw new Exception(s"Could not parse query: ${JsError.toJson(e)}")
    }

    val maybeToken = for {
      result <- dbI.checkPswd(credentials.email, credentials.password)
      token <- if (result) {
        generateToken(credentials.email)
      } else {
        // maybePlayer expects a future. Wrap result in future.successful
        Future.successful(None)
      }
    } yield {
      token
    }

    maybeToken.map( result => {
      result match {
        case Some(token) => {
          val response = Json.toJson(
            Json.toJson(AuthResponse(token))
          )
          Ok(response)
        }
        case None => Unauthorized("Invalid username or password")
      }
    })
  }

  /**
   * Attempts to generate a token based on the email passed in. When generating the token
   * it is assumed that validation has already ocurred.
   *
   * @param email The email for the account we are generating the token for
   * @return A option that may contain a JWT token
   */
  private def generateToken(email: String): Future[Option[String]] = {
    for {
      maybePlayer <- dbI.getPlayer(email)
    } yield {
      maybePlayer match {
        case Some(player) =>
          Some(TokenUtility.createJWT(player, secret))
        case None => None
      }
    }
  }
}

