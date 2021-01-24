package actors

import akka.actor._

object Phase1Actor {
  def props(out: ActorRef) = Props(new Phase1Actor(out))
}

/** Security Selector Actor
 *
 * @param out
 */
class Phase1Actor(out: ActorRef) extends Actor {
  def receive = {
//    case msg: Start =>
//      // Get security options
//      // Send options to out
//    case msg:

    case _ =>
      out ! ("unknown message!")
  }
}