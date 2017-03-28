package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}

/** Server is the main HTTP server logic for the service.
  */
object Server {
  /** Starts the server
    *
    * @return A handle to the server instance to be used for shutdown.
    */
  def start(): ServerHandle = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    // Load up the routes
    val router = Router()

    // Bind and listen on the configured port
    val bindingFuture = Http().bindAndHandleSync(router, "localhost", 8080)

    ServerHandleImpl(bindingFuture, system)
  }
}

private case class ServerHandleImpl(private val bindingFuture: Future[ServerBinding], private val system: ActorSystem) extends ServerHandle {
  def stop()(implicit executionContext: ExecutionContext): Future[Unit] = {
    bindingFuture.flatMap(_.unbind()).andThen{
      case _ =>system.terminate()
    }
  }
}

/** ServerHandle represents an active server instance.
  */
trait ServerHandle {
  /** stop causes the server instance to shut down safely.
    *
    * @param executionContext The execution context to use
    * @return A future which completes when shutdown is finished
    */
  def stop()(implicit executionContext: ExecutionContext): Future[Unit]
}