package storage

import akka.actor.Actor
import models.invoices.{Client, Invoice, InvoiceCollection}
import storage.connectors.InvoiceConnector

object InvoiceStore {
  case object GetInvoices
  case class GetInvoice(id: String)
  case class SaveInvoice(invoice: Invoice)
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
  }
}
