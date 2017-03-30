package server.handlers

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.pattern.ask
import akka.util.Timeout
import models.invoices.{Invoice, InvoiceCollection}
import spray.json._
import server.json.Protocol._
import storage.InvoiceStore
import storage.connectors.{DynamoDBConnector, InvoiceConnector}

import scala.concurrent.duration._

class InvoicesHandler extends Actor {
  private val invoiceConnector: InvoiceConnector = DynamoDBConnector
  private val invoiceStorage = context.actorOf(Props(new InvoiceStore(invoiceConnector)))
  private implicit val executionContext = context.dispatcher
  private implicit val askTimeout = Timeout(30 seconds)

  def receive: Receive = {
    case HttpRequest(GET, Uri.Path("/invoices"), _, _, _) =>
      val asker = sender()

      val fInvoices = (invoiceStorage ? InvoiceStore.GetInvoices).mapTo[InvoiceCollection]
      fInvoices.map { invoices =>
        val response = JsonResponse(invoices).toJson.prettyPrint
        asker ! HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, response))
      }
    case _ =>
      sender ! HttpResponse(status = StatusCodes.NotFound)
  }
}
