package da

import org.mindrot.jbcrypt.BCrypt
import sun.security.util.Password

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DatabaseInterface @Inject() (val db: SecurityTraderDatabase) (implicit ec: ExecutionContext){
  def createUser(email: String, userName: String, password: String): Future[Boolean] = {
    // check to see if email is available
    for {
      userExist <- db.userExists(email)
      success <- if (userExist) {
        // Warn user that name is not available
        // throw new Exception("This email is already taken. Please use try again.")
        Future.successful(false)
      } else {
        // User does not exist so create User
        //encrypt password first
        val ePass = BCrypt.hashpw(password, BCrypt.gensalt())
        db.createUser(email, userName, ePass)
      }
    } yield {
      success
    }
  }

  def checkPswd(email: String,password: String): Future[Boolean] = {
    for {
      maybeHashedPswd <- db.getHashedPswd(email)   //user login will pass hashed pswd from DB
    } yield {
        maybeHashedPswd match {
          case Some(hashedPswd) => BCrypt.checkpw(password, hashedPswd)
          case _ => false
        }
      }
  }
}
