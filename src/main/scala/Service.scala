import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.Uri
import server.{Router, Server}
import handlers.{IndexHandler, InvoicesHandler}
import storage.connectors.{DynamoDBConnector, InvoiceConnector}

import scala.io.StdIn
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Service is the main entry point for the application
  */
object Service {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()

    val server = new Server(Router(routes))
    val startup =  server.start()

    startup.map { _ =>
      println("Server running. Press ENTER to stop.")
      StdIn.readLine()
    }.flatMap { _ =>
      println("Shutting down...")
      server.stop()
    }.map{ _ =>
      system.terminate()
    }.onComplete { _ =>
      println("Goodbye.")
    }
  }

  private val invoiceConnector: InvoiceConnector = DynamoDBConnector

  private def routes(implicit system: ActorSystem): Map[Uri.Path, ActorRef] = Map(
    Uri.Path("/") -> system.actorOf(Props[IndexHandler]),
    Uri.Path("/invoices") -> system.actorOf(Props(new InvoicesHandler(invoiceConnector)))
  )
}
