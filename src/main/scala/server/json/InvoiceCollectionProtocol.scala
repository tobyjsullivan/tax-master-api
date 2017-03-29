package server.json

import models.invoices.InvoiceCollection
import spray.json._

trait InvoiceCollectionProtocol extends DefaultJsonProtocol with InvoiceProtocol {
  implicit object InvoiceCollectionFormat extends RootJsonFormat[InvoiceCollection] {
    def write(collection: InvoiceCollection): JsValue = collection.invoices.toJson

    def read(json: JsValue): InvoiceCollection = throw new NotImplementedError("Method not implemented")
  }
}
