package server

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._

/** Builds the route handlers and forwards all requests to the correct actors
  */
object Router {
  def apply(): HttpRequest => HttpResponse = {
    case HttpRequest(GET, _, _, _, _) =>
      HttpResponse(entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Hello world!"))
  }
}
