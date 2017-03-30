package storage

import java.util.UUID

import akka.actor.Actor
import models.invoices.Invoice
import storage.connectors.InvoiceConnector

object InvoiceStore {
  case object GetInvoices
  case class GetInvoice(id: String)
  case class SaveInvoice(invoice: Invoice)
  case class InvoiceSaved(invoice: Invoice)
}

class InvoiceStore(connector: InvoiceConnector) extends Actor {
  import InvoiceStore._

  def receive = {
    case GetInvoices =>
      val invoices = connector.getAllInvoices()
      sender ! invoices
    case GetInvoice(id) =>
      val invoice = connector.getInvoice(id)
      sender ! invoice
    case SaveInvoice(invoice) =>
      // Generate an id for new clients
      val clientWithId = invoice.client.id match {
        case Some(id) => invoice.client
        case None => invoice.client.copy(id = Some(UUID.randomUUID().toString))
      }

      // Generate an id for the invoice
      val invoiceWithId = invoice.copy(id = Some(UUID.randomUUID().toString), client = clientWithId)

      connector.saveInvoice(invoiceWithId)
      sender ! InvoiceSaved(invoiceWithId)
  }
}
