package server

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.pattern.ask
import akka.util.Timeout
import server.handlers.InvoicesHandler
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/** Builds the route handlers and forwards all requests to the correct actors
  */
object Router {
  def apply()(implicit system: ActorSystem): HttpRequest => HttpResponse = {
    case r @ HttpRequest(GET, Uri.Path("/invoices"), _, _, _) =>
      implicit val timeout = Timeout( 30 seconds)
      implicit val ec = system.dispatcher

      var act = system.actorOf(Props[InvoicesHandler])
      val f: Future[HttpResponse] = (act ? r).map[HttpResponse] {
        case resp @ HttpResponse(_, _, _, _) => resp
        case _ => HttpResponse(status = 500)
      }
      Await.result[HttpResponse](f, timeout.duration)
    case HttpRequest(GET, _, _, _, _) =>
      HttpResponse(entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Hello world!"))
  }
}
