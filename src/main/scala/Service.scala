import server.Server

import scala.io.StdIn
import scala.concurrent.duration._

/**
  * Service is the main entry point for the application
  */
object Service {
  import scala.concurrent.ExecutionContext.Implicits.global

  def main(args: Array[String]): Unit = {
    val serverHandle = Server.start()

    println("Server running. Press ENTER to stop.")
    StdIn.readLine()

    scala.concurrent.Await.ready(serverHandle.stop(), 30 seconds)
  }
}
