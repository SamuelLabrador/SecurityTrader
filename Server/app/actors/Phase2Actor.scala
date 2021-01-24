package actors

import akka.actor._

object Phase2Actor {
  def props(out: ActorRef) = Props(new Phase2Actor(out))
}

class Phase2Actor(out: ActorRef) extends Actor {
  def receive = {
    case msg: String =>
      out ! ("Phase2Actor received message: " + msg)
  }
}
