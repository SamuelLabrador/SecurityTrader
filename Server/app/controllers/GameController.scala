package controllers

import actors.WebSocketActor
import akka.actor.ActorSystem

import javax.inject._
import play.api._
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import akka.actor.ActorSystem
import akka.stream.Materializer


@Singleton
class GameController @Inject() (val controllerComponents: ControllerComponents)
                               (implicit system: ActorSystem, mat: Materializer)
  extends BaseController {

  def socket() = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      WebSocketActor.props(out)
    }
  }
}
