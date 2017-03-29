package server.json

import models.invoices.{Invoice, InvoiceCollection}
import org.scalatest._
import spray.json._

class InvoiceCollectionProtocolSpec extends FunSpec with Matchers with InvoiceCollectionProtocol {
  describe("invoiceCollectionFormat") {
    describe(".write") {
      describe("with an empty InvoiceCollection") {
        val col = InvoiceCollection(Seq())

        it("should produce an empty JSON array") {
          val json = col.toJson

          json should be (JsArray())
        }
      }
    }
  }
}
