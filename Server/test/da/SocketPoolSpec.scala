package da

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec

class SocketPoolSpec
  extends PlaySpec
  with BeforeAndAfterEach {

  var mockSocketPool: SocketPool = null

  // re-instantiate our mock object each test run
  override def beforeEach() {
    mockSocketPool = new SocketPoolImpl()
  }

  "SocketPool" should {
    "be able to instantiate a new pool and generate unique ids" in {
      mockSocketPool.createPool() mustEqual 1L
      mockSocketPool.createPool() mustEqual 2L
      mockSocketPool.createPool() mustEqual 3L
    }

    // TODO: Add more unit tests to increase code coverage.
  }

}
