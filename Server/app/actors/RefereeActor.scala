package actors

import akka.actor._

object RefereeActor {
  def props(out: ActorRef) = Props(new WebSocketActor(out))
}

class RefereeActor(out: ActorRef) extends Actor {
  def receive = {
    case msg: String =>
      out ! ("referee has received your message")
  }
}
