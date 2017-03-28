package server.handlers

import akka.actor.Actor
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import invoices.{InvoiceStore}
import spray.json._
import server.json.Protocol._

class InvoicesHandler extends Actor {
  def receive: Receive = {
    case HttpRequest(GET, Uri.Path("/invoices"), _, _, _) =>

      val invoices = InvoiceStore.getAll()

      val response = JsonResponse(invoices).toJson.prettyPrint

      sender() ! HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, response))
  }
}
