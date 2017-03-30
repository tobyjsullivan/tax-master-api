package server

import akka.actor.{ActorRef}
import akka.http.scaladsl.model._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/** Builds the route handlers and forwards all requests to the correct actors
  */
case class Router(routes: Map[Uri.Path, ActorRef]) {
  def route(req: HttpRequest)(implicit ec: ExecutionContext): Future[HttpResponse] = handler(req) match {
    case Some(actor) =>
      implicit val askTimeout = Timeout( 30 seconds)
      (actor ? req).mapTo[HttpResponse]
    case None => Future{HttpResponse(status = StatusCodes.NotFound)}
  }

  def handler(req: HttpRequest): Option[ActorRef] = req match {
    case HttpRequest(_, path, _, _, _) => routes.get(path.path)
    case _ => None
  }
}
