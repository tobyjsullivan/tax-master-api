package server.handlers

import akka.actor.Actor
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, HttpResponse}
import akka.http.scaladsl.model.HttpMethods._

class IndexHandler extends Actor {
  def receive: Receive = {
    case HttpRequest(GET, _, _, _, _) =>
      sender ! HttpResponse(entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Hello world!"))
  }
}
