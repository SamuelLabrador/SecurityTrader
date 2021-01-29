package da

import akka.actor.ActorRef
import com.google.inject.ImplementedBy

import javax.inject.Singleton

@ImplementedBy(classOf[SocketPoolImpl])
trait SocketPool {
  def getConnections(poolId: Long): Seq[ActorRef]
  def addConnection(poolId: Long, client: ActorRef): Boolean
  def createPool(): Long
  def createPool(client: ActorRef): Long
  def deletePool(poolId: Long): Boolean
}

@Singleton
class SocketPoolImpl extends SocketPool {
  private[SocketPoolImpl] var id: Long = 0
  private[SocketPoolImpl] var pools: collection.mutable.Map[Long, Seq[ActorRef]] = collection.mutable.Map[Long, Seq[ActorRef]]()

  def getConnections(poolId: Long): Seq[ActorRef] = {
    if (!this.pools.contains(poolId))
      throw new Exception("Could not find pool id")

    this.pools(poolId)
  }

  /** Adds client actor to corresponding pool
   * @param poolId unique pool id
   * @param client actorRef of client
   * @return Boolean value - Success or not
   */
  def addConnection(poolId: Long, client: ActorRef): Boolean = {
    if (!this.pools.contains(poolId))
      throw new Exception("Could not find pool id")

    this.pools(poolId) = this.pools(poolId) ++ Seq(client)
    true
  }

  private def generateUniqueId(): Long = {
    this.id += 1
    this.id
  }

  def createPool(): Long = {
    val newId = generateUniqueId()
    pools += newId -> Seq()
    newId
  }

  def createPool(client: ActorRef): Long = {
    val newId = generateUniqueId()
    pools += id -> Seq(client)
    newId
  }

  def deletePool(poolId: Long): Boolean = {
    if (!this.pools.contains(poolId))
      throw new Exception("Could not find pool id")

    this.pools.remove(poolId)
    true
  }
}
