package da

import javax.inject.{Inject, Singleton}
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.concurrent.CustomExecutionContext
import akka.actor.ActorSystem
import models.rest.Player
import play.api.Logger
import sun.security.util.Password

import scala.util.{Failure, Success}

// For use of when ExecutionContext is not in its default form
class DatabaseExecutionContext @Inject()(system: ActorSystem)
  extends CustomExecutionContext(system, "database-dispatcher") {
}

// Implicit ExecutionContext in its default form
// Singleton allows for only one instance of class
@Singleton
class SecurityTraderDatabase @Inject() (db: Database) (implicit ec: ExecutionContext) {

  val log = Logger(this.getClass)

  def userExists(email: String): Future[Boolean] = {
     Future {
      db.withConnection { conn =>
        // check for username
        val statement = "SELECT COUNT(email) FROM User WHERE email LIKE ?"
        val prepStatement = conn.prepareStatement(statement)
        prepStatement.setString(1, email)
        val results = prepStatement.executeQuery()
        results.next()
        val count = results.getInt(1)
        count != 0
      }
    }
  }

//  def updateSomething(): Unit = {
//    Future {
//      db.withConnection { conn =>
//        // Establish connection with database and create Statement object for query usage
//        val statement = conn.createStatement()
//        // statement.executeUpdate("INSERT INTO User (email, username, password, isMod) VALUES ('temp@gmail.com', 'temp', 'temppswd', 0)")
//
//      }
//    }
//  }

  def createUser(email: String, userName: String, password: String): Future[Boolean] = {
    Future {
      db.withConnection { conn =>
        // Only executes if Username is available
        // Establish connection with database and create Statement object for query usage
        val statement = "INSERT INTO User (email, username, password, isMod) VALUES (?,?,?,?)"
        val prepStatement = conn.prepareStatement(statement)

        prepStatement.setString(1, email)
        prepStatement.setString(2, userName)
        prepStatement.setString(3, password)
        prepStatement.setInt(4, 0)
        val result = prepStatement.executeUpdate()
        result == 1
      }
    }
  }

  def getHashedPswd(email: String): Future[Option[String]] = {
    Future {
      db.withConnection { conn =>
        val statement = "SELECT password FROM User WHERE email LIKE ?"
        val prepStatement = conn.prepareStatement(statement)

        prepStatement.setString(1, email)
        val results = prepStatement.executeQuery()
        if (results.next()){
          Some(results.getString("password"))
        } else {
          None
        }

      }
    }
  }

  def getUser(email: String) : Future[Option[Player]] = {
    Future {
      db.withConnection { conn =>
        try {
          val statement = "SELECT * FROM User WHERE email LIKE ?"
          val prepStatement = conn.prepareStatement(statement)

          prepStatement.setString(1, email)

          val result = prepStatement.executeQuery()
          result.next()

          val id = result.getString("id")
          val username = result.getString("username")
          val isMod = result.getBoolean("isMod")

          Some(Player(id, Some(email), username, isMod))
        } catch {
          case e: Throwable =>
            log.error(s"An exception ocurred while trying to get the user: ${e}")
            None
        }
      }
    }
  }
}
