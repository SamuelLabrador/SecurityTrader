package da

import javax.inject.{Inject, Singleton}
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.concurrent.CustomExecutionContext
import akka.actor.ActorSystem
import sun.security.util.Password

// For use of when ExecutionContext is not in its default form
class DatabaseExecutionContext @Inject()(system: ActorSystem)
  extends CustomExecutionContext(system, "database-dispatcher") {
}

// Implicit ExecutionContext in its default form
// Singleton allows for only one instance of class
@Singleton
class SecurityTraderDatabase @Inject() (db: Database) (implicit ec: ExecutionContext) {

  def findUser(userName: String): Unit= {
     Future {
      db.withConnection { conn =>
        // check for username
        val statement = "SELECT ? FROM User"
        val prepStatement = conn.prepareStatement(statement)
        prepStatement.setString(1, userName)
        val results = prepStatement.executeUpdate()

      }
    }
  }

  def updateSomething(): Unit = {
    Future {
      db.withConnection { conn =>
        // Establish connection with database and create Statement object for query usage
        val statement = conn.createStatement()
        // statement.executeUpdate("INSERT INTO User (email, username, password, isMod) VALUES ('temp@gmail.com', 'temp', 'temppswd', 0)")
      }
    }
  }

  def createUser(email: String, userName: String, password: String): Unit = {
    Future {
      db.withConnection { conn =>
        //check to see if User Name is available
        val result  = findUser(email)
        if (result. ){
            // Warn user that name is not available
            print("This username is already taken. Please use another name.")
        }

        // Only executes if Username is available
        // Establish connection with database and create Statement object for query usage
        else {
          val statement = "INSERT INTO User (email, username, password, isMod) VALUES (?,?,?,?)"
          val prepStatement = conn.prepareStatement(statement)

          prepStatement.setString(1, email)
          prepStatement.setString(2, userName)
          prepStatement.setString(3, password)
          prepStatement.setInt(4, 0)
          prepStatement.executeUpdate()
        }
      }
    }
  }
}
