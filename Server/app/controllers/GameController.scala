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
import play.api.libs.json.{JsValue, Json}
import utils.SameOriginCheck

import java.time.ZonedDateTime
import java.net.InetAddress
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class GameController @Inject() (playerParentActor: ActorRef[PlayerParentActor.Create],
                                refereeParentActor: ActorRef[RefereeParentActor.Create],
                                cc: ControllerComponents)
                               (implicit ec: ExecutionContext, scheduler: Scheduler)
  extends AbstractController(cc) {

  val logger = play.api.Logger(getClass)

  def socket = WebSocket.acceptOrResult[JsValue, JsValue] {
    case request =>
      wsFutureFlow(request).map { flow =>
        Right(flow)
      }.recover {
        case e: Exception =>
          logger.error("Cannot create websocket", e)
          val jsError = Json.obj("error" -> "Cannot create websocket")
          val result = InternalServerError(jsError)
          Left(result)
      }
  }

  /**
   * Creates a Future containing a Flow of JsValue in and out.
   */
  private def wsFutureFlow(request: RequestHeader): Future[Flow[JsValue, JsValue, NotUsed]] = {
    // Use guice assisted injection to instantiate and configure the child actor.

    // Ask requires a timeout, if the timeout hits without response
    // the ask is failed with a TimeoutException
    implicit val timeout = Timeout(1.second) // the first run in dev can take a while :-(
    playerParentActor.ask(replyTo => PlayerParentActor.Create(request.id.toString, replyTo))
  }

  def createGame = Action.async { implicit request: Request[AnyContent] =>
    createRefereeActor(request).map(Ok(_))
  }

  private def createRefereeActor(request: RequestHeader): Future[String] = {
    implicit val timeout = Timeout(1.second)
    refereeParentActor.ask(replyTo => RefereeParentActor.Create(request.id.toString, replyTo))
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
