package actors

import akka.actor._

object Phase3Actor {
  def props(out: ActorRef) = Props(new Phase3Actor(out))
}

class Phase3Actor(out: ActorRef) extends Actor {
  def receive = {
    case msg: String =>
      out ! ("Phase 3 Actor received your message " + msg)
  }
}
