package utils

import models.rest.Player
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson}
import play.api.Logger
import play.api.libs.json.{JsObject, Json}

import java.time.Clock
import scala.util.{Failure, Success, Try}

// See http://pauldijou.fr/jwt-scala/samples/jwt-play-json/

/**
 * Generates and decodes JSON Web Tokens (JWT)
 */
object TokenUtility {
  implicit val clock = Clock.systemUTC
  val log = Logger(this.getClass)
  val algo = JwtAlgorithm.HS256

  // Amount of seconds before the token expires -- 1 day
  val tokenExpiration = 60 * 60 * 24

  /**
   * Creates a JSON Web Token
   *
   * @param player Player data to embed in the token
   * @param secret the http secret used to sign the token
   * @return The token
   */
  def createJWT(player: Player, secret: String): String = {

    // Create payload of the data we want to track
    val payload = Json.toJson(player).as[JsObject]

    // Set expiration for token
    val claim = JwtClaim(payload.toString).issuedNow.expiresIn(tokenExpiration)

    JwtJson.encode(claim, secret, algo)
  }

  /**
   * Attempts to verify and decode a JSON Web Token.
   *
   * @param token the token to be verified
   * @param secret the http secret used to sign the token
   * @return Player data if verification and decode are successful
   */
  def decodeJWT(token: String, secret: String): Try[Player] = {
    JwtJson.decodeJson(token, secret, Seq(algo)) match {
      case Success(claim) =>
        Try(claim.as[Player])
      case Failure(e) =>
        // Propagate failure
        log.error(s"Could not decode JWT. Got exception: ${e}")
        Failure(e)
    }
  }
}