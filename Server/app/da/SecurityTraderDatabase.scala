package da

import javax.inject.{Inject, Singleton}
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.concurrent.CustomExecutionContext
import akka.actor.ActorSystem

// For use of when ExecutionContext is not in its default form
class DatabaseExecutionContext @Inject()(system: ActorSystem)
  extends CustomExecutionContext(system, "database-dispatcher") {
}

// Implicit ExecutionContext in its default form
// Singleton allows for only one instance of class
@Singleton
class SecurityTraderDatabase @Inject() (db: Database) (implicit ec: ExecutionContext) {
  def updateSomething(): Future[Unit] = {
    Future {
      val result = db.withConnection { conn =>
        // Establish connection with database and create Statement object for query usage
        val statement = conn.createStatement()

        val rowsUpdate = statement.executeUpdate("INSERT INTO User " +
          "(email, username, password, isMod) " +
          "VALUES ('temp@gmail.com', 'temp', 'temppswd', 0)"
        )

        statement.executeQuery("SELECT * FROM User WHERE 1")
      }
    }
  }
}
