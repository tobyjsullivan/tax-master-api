package server.handlers

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.pattern.ask
import akka.util.Timeout
import models.invoices.Invoice
import spray.json._
import server.json.Protocol._
import storage.InvoiceStore
import storage.connectors.InvoiceConnector

import scala.concurrent.duration._

class InvoicesHandler extends Actor {
  private val invoiceConnector: InvoiceConnector = ???
  private val invoiceStorage = context.actorOf(Props(new InvoiceStore(invoiceConnector)))
  private implicit val executionContext = context.dispatcher
  private implicit val askTimeout = Timeout(30 seconds)

  def receive: Receive = {
    case HttpRequest(GET, Uri.Path("/models/invoices"), _, _, _) =>
      (invoiceStorage ? InvoiceStore.GetInvoices).mapTo[Seq[Invoice]].map { invoices =>
        val response = JsonResponse(invoices).toJson.prettyPrint
        sender ! HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, response))
      }
    case _ =>
      sender ! HttpResponse(status = StatusCodes.NotFound)
  }
}
