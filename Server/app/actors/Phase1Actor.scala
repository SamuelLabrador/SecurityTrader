package actors

import akka.actor._

object Phase1Actor {
  def props(out: ActorRef) = Props(new Phase1Actor(out))
}

class Phase1Actor(out: ActorRef) extends Actor {
  def receive = {
    case msg: String =>
      out ! ("phase1 actor received message " + msg)
  }
}
