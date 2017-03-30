package server

import akka.actor.{ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}

/** Server is the main HTTP server logic for the service.
  */
class Server(router: Router) {
  var bindingFuture: Option[Future[ServerBinding]] = None

  /** Starts the server
    *
    * @return A handle to the server instance to be used for shutdown.
    */
  def start()(implicit system: ActorSystem): Future[Unit] = {
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    // Bind and listen on the configured port
    val fBinding = Http().bindAndHandleAsync(router.route, interface = "localhost", port = 8080)

    bindingFuture = Some(fBinding)

    // Return Unit to avoid leaking any internal state
    fBinding.map(_ => Unit)
  }

  def stop()(implicit executionContext: ExecutionContext): Future[Unit] = bindingFuture match {
    case Some(fBinding) =>
      bindingFuture = None
      fBinding.flatMap(_.unbind())
    case None => Future.successful()
  }
}
