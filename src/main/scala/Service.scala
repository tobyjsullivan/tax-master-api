import server.Server

import scala.io.StdIn
import scala.concurrent.duration._

/**
  * Service is the main entry point for the application
  */
object Service {
  import scala.concurrent.ExecutionContext.Implicits.global

  def main(args: Array[String]): Unit = {
    val startup = Server.start()

    startup.map { _ =>
      println("Server running. Press ENTER to stop.")
      StdIn.readLine()
    }.flatMap { _ =>
      println("Shutting down...")
      Server.stop()
    }.onComplete { _ =>
      println("Goodbye.")
    }
  }
}
