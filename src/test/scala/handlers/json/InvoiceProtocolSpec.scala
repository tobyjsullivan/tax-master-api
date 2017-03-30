package handlers.json

import models.invoices.{Client, Invoice}
import org.joda.money.{CurrencyUnit, Money}
import org.joda.time.LocalDate
import org.scalatest._
import spray.json._

class InvoiceProtocolSpec extends FunSpec with Matchers with InvoiceProtocol {
  describe("invoiceFormat") {
    describe(".write") {
      describe("with an Invoice") {
        val invoice = Invoice(
          id = "182b2349-c69f-42b4-ae9d-1e54148e7df7",
          client = Client(
            id = "5022e8fa-3268-405a-8180-93cbc3290e92",
            name = "Initech"
          ),
          issueDate = new LocalDate(2009, 8, 28),
          amount = Money.of(
            CurrencyUnit.EUR,
            4061.00
          )
        )

        it("should serialize to the expected JSON") {
          val json = invoice.toJson

          json should be (
            JsObject(
              "id" -> JsString("182b2349-c69f-42b4-ae9d-1e54148e7df7"),
              "client" -> JsObject(
                "id" -> JsString("5022e8fa-3268-405a-8180-93cbc3290e92"),
                "name" -> JsString("Initech")
              ),
              "issueDate" -> JsString("2009-08-28"),
              "amount" -> JsObject(
                "currency" -> JsString("EUR"),
                "value" -> JsNumber(406100)
              )
            )
          )
        }
      }
    }

    describe(".read") {
      describe("with a valid JSON value") {
        val json =
          """{
            | "id": "40a30c8f-a4b4-4546-90ef-6c083692149e",
            | "client": {
            |   "id": "e76b982c-7224-4d3b-b52a-915ad811c544",
            |   "name": "Massive Dynamic"
            | },
            | "issueDate": "2014-08-19",
            | "amount": {
            |   "currency": "CAD",
            |   "value": 392024
            | }
            |}""".stripMargin.parseJson

        it("should parse the Invoice") {
          val invoice = json.convertTo[Invoice]

          invoice should be (
            Invoice(
              id = "40a30c8f-a4b4-4546-90ef-6c083692149e",
              client = Client(
                id = "e76b982c-7224-4d3b-b52a-915ad811c544",
                name = "Massive Dynamic"
              ),
              issueDate = new LocalDate(2014, 8, 19),
              amount = Money.of(CurrencyUnit.CAD, 3920.24)
            )
          )
        }
      }

      describe("with an invalid amount") {
        val json =
          """{
            | "id": "40a30c8f-a4b4-4546-90ef-6c083692149e",
            | "client": {
            |   "id": "e76b982c-7224-4d3b-b52a-915ad811c544",
            |   "name": "Massive Dynamic"
            | },
            | "issueDate": "2014-08-19",
            | "amount": {
            |   "currency": "CAD"
            | }
            |}""".stripMargin.parseJson

        it ("should throw a DeserializationException") {
          assertThrows[DeserializationException] {
            json.convertTo[Invoice]
          }
        }
      }
    }
  }
}
