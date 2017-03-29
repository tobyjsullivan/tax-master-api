package storage.connectors

import models.invoices.{Invoice, InvoiceCollection}

trait InvoiceConnector {
  def getAllInvoices(): InvoiceCollection

  def getInvoice(id: String): Option[Invoice]

  def saveInvoice(invoice: String): Unit
}
