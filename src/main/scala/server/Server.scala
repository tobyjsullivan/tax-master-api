package server

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}

/** Server is the main HTTP server logic for the service.
  */
object Server {
  var actorSystem: Option[ActorSystem] = None
  var bindingFuture: Option[Future[ServerBinding]] = None

  /** Starts the server
    *
    * @return A handle to the server instance to be used for shutdown.
    */
  def start(): Future[Unit] = {
    implicit val system = actorSystem.getOrElse(ActorSystem())
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    // Load up the routes
    val router = new Router(system)

    // Bind and listen on the configured port
    val fBinding = Http().bindAndHandleAsync(router.apply, "localhost", 8080)

    bindingFuture = Some(fBinding)
    actorSystem = Some(system)

    // Return Unit to avoid leaking any internal state
    fBinding.map(_ => Unit)
  }

  def stop()(implicit executionContext: ExecutionContext): Future[Unit] = {
    unbind().flatMap(_ => shutdown())
  }

  private def unbind()(implicit executionContext: ExecutionContext): Future[Unit] = bindingFuture match {
    case Some(fBinding) =>
      bindingFuture = None
      fBinding.flatMap(_.unbind())
    case None => Future.successful()
  }

  private def shutdown()(implicit executionContext: ExecutionContext): Future[Unit] = actorSystem match {
    case Some(system) =>
      actorSystem = None
      system.terminate().map(_ => ())
    case None => Future.successful()
  }
}
