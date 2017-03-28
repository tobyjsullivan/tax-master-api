package server.json

import invoices.Invoice
import spray.json._

private[json] trait InvoiceProtocol extends DefaultJsonProtocol
  with ClientProtocol
  with LocalDateProtocol
  with MoneyProtocol {
  implicit val invoiceFormat: RootJsonFormat[Invoice] = jsonFormat4(Invoice)
}
