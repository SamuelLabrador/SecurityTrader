package controllers

import actors.{PlayerParentActor, RefereeParentActor}
import akka.NotUsed

import javax.inject._
import play.api.mvc._
import akka.actor.typed.{ActorRef, Scheduler}
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import models.rest.ServerStatus
import play.api.Logger
import play.api.libs.json.{JsValue, Json}

import java.time.ZonedDateTime
import java.net.InetAddress
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class GameController @Inject() (playerParentActor: ActorRef[PlayerParentActor.Message],
                                refereeParentActor: ActorRef[RefereeParentActor.Message],
                                cc: ControllerComponents)
                               (implicit ec: ExecutionContext, scheduler: Scheduler)
  extends AbstractController(cc) {

  val logger: Logger = play.api.Logger(getClass)

  def authSocket(username: String, token: String) = {
    // TODO: Update the user's username in the database if different
    createWebSocketFlow(username)
  }

  def anonSocket(username: String) = {
    // TODO: Generate token for anonymous user
    createWebSocketFlow(username)
  }

  private def createWebSocketFlow(username: String) = {
    WebSocket.acceptOrResult[JsValue, JsValue] {
      case request =>
        wsFutureFlow(request, username).map { flow =>
          Right(flow)
        }.recover {
          case e: Exception =>
            logger.error("Cannot create websocket", e)
            val jsError = Json.obj("error" -> "Cannot create websocket")
            val result = InternalServerError(jsError)
            Left(result)
        }
    }
  }

  /**
   * Creates a Future containing a Flow of JsValue in and out.
   */
  private def wsFutureFlow(request: RequestHeader, username: String): Future[Flow[JsValue, JsValue, NotUsed]] = {
    // Use guice assisted injection to instantiate and configure the child actor.
    logger.error(s"Username: $username")


    // Ask requires a timeout, if the timeout hits without response
    // the ask is failed with a TimeoutException
    implicit val timeout = Timeout(1.second) // the first run in dev can take a while :-(
    playerParentActor.ask(replyTo => PlayerParentActor.Create(request.id.toString, username, refereeParentActor, replyTo))
  }

  /**
   * @return Json containing current server time and ip address
   */
  def status = Action { implicit request: Request[AnyContent] =>
    val ip = InetAddress.getLocalHost
    val value = Json.toJson(ServerStatus(ZonedDateTime.now().toInstant.toEpochMilli, ip.getHostAddress))
    Ok(value)
  }
}
