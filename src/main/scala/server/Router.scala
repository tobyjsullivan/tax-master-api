package server

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.pattern.ask
import akka.util.Timeout
import server.handlers.{IndexHandler, InvoicesHandler}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/** Builds the route server.handlers and forwards all requests to the correct actors
  */
class Router(system: ActorSystem) {
  private val indexHandler = system.actorOf(Props[IndexHandler])
  private val invoicesHandler = system.actorOf(Props[InvoicesHandler])
  implicit val askTimeout = Timeout( 30 seconds)

  private val routeMap: Map[Uri.Path, ActorRef] = Map(
    Uri.Path("/") -> indexHandler,
    Uri.Path("/invoices") -> invoicesHandler
  )

  def apply(req: HttpRequest)(implicit ec: ExecutionContext): Future[HttpResponse] = handler(req) match {
    case Some(actor) => (actor ? req).mapTo[HttpResponse]
    case None => Future{HttpResponse(status = StatusCodes.NotFound)}
  }

  def handler(req: HttpRequest): Option[ActorRef] = req match {
    case HttpRequest(_, path, _, _, _) => routeMap.get(path.path)
    case _ => None
  }
}
