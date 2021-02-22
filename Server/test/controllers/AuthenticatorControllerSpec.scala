package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.mvc.Result
import play.api.test.Helpers.contentAsString
import play.api.test.{FakeRequest, Helpers, Injecting}

import scala.concurrent.Future

class AuthenticatorControllerSpec extends PlaySpec
  with GuiceOneAppPerTest
  with Injecting
  with BeforeAndAfterAll{
  // TODO understand if it is better to make a test method for each method
  // TODO perhaps methods are not allowed

    "createUser" should {
      "Should return the value of the created user " in {
        val mockDataBaseInterface = mock[]
        val controller             = new AuthenticatorController( , Helpers.stubControllerComponents())
        val result: Future[Result] = controller.createUser().apply(FakeRequest())
        val bodyText: String       = contentAsString(result)
        bodyText mustBe "ok"
      }
    }

}
