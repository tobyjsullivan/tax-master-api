package handlers

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers.Location
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import models.invoices.{Invoice, InvoiceCollection}
import spray.json._
import json.Protocol._
import storage.InvoiceStore
import storage.connectors.{DynamoDBConnector, InvoiceConnector}

import scala.concurrent.duration._

class InvoicesHandler(invoiceConnector: InvoiceConnector) extends Actor {
  private val invoiceStorage = context.actorOf(Props(new InvoiceStore(invoiceConnector)))
  private implicit val executionContext = context.dispatcher
  private implicit val materializer = ActorMaterializer()
  private implicit val timeout = Timeout(30 seconds)

  private val corsHeaders = List(
    headers.`Access-Control-Allow-Origin`.*,
    headers.`Access-Control-Allow-Methods`.apply(GET, POST),
    headers.`Access-Control-Allow-Headers`.apply("Content-Type")
  )

  def receive: Receive = {
    case HttpRequest(GET, Uri.Path("/invoices"), _, _, _) =>
      val asker = sender()

      val fInvoices = (invoiceStorage ? InvoiceStore.GetInvoices).mapTo[InvoiceCollection]
      fInvoices.map { invoices =>
        val response = JsonResponse(invoices).toJson.prettyPrint
        asker ! HttpResponse(
          headers = List(corsHeaders: _*),
          entity = HttpEntity(ContentTypes.`application/json`, response)
        )
      }
    case req @ HttpRequest(POST, Uri.Path("/invoices"), _, _, _) =>
      val asker = sender()

      req.entity.toStrict(timeout.duration).flatMap { strictEntity =>
        val jsString = strictEntity.data.utf8String
        val json = jsString.parseJson
        val invoice = json.convertTo[Invoice]

        invoiceStorage ? InvoiceStore.SaveInvoice(invoice)
      }.mapTo[InvoiceStore.InvoiceSaved].map { msg =>
        val invoice = msg.invoice
        val response = JsonResponse(invoice).toJson.prettyPrint

        val locationHeader = Location(Uri.apply(s"/invoices/${invoice.id.get}"))

        asker ! HttpResponse(
          status = StatusCodes.Created,
          headers = List(
            corsHeaders: _*
          ) :+ locationHeader,
          entity = HttpEntity(ContentTypes.`application/json`, response)
        )
      }
    case HttpRequest(OPTIONS, _, _, _, _) =>
      val asker = sender()

      asker ! HttpResponse(headers = List(corsHeaders: _*))
    case _ =>
      sender ! HttpResponse(status = StatusCodes.NotFound)
  }
}
