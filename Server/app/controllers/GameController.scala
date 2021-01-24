package controllers

import actors.WebSocketActor
import akka.actor.ActorSystem

import javax.inject._
import play.api._
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import akka.actor.ActorSystem
import akka.stream.Materializer
import da.SocketPool
import models.rest.ServerStatus
import play.api.libs.json.{JsValue, Json}

import java.time.ZonedDateTime
import java.net.InetAddress


@Singleton
class GameController @Inject() (val controllerComponents: ControllerComponents,
                                implicit val socketPool: SocketPool)
                               (implicit system: ActorSystem, mat: Materializer)
  extends BaseController {

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      WebSocketActor.props(socketPool, out)
    }
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
