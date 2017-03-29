package server.json

import org.joda.money.{CurrencyUnit, Money}
import org.joda.money.CurrencyUnit._
import org.scalatest._
import spray.json._

class MoneyProtocolSpec extends FunSpec with Matchers with MoneyProtocol {
  describe("moneyFormat") {
    describe(".write") {
      describe("with a Money object") {
        val money = Money.of(AUD, 8771.24)

        it("should serialize to the expected JSON") {
          val json = money.toJson

          json should be (
            JsObject(
              "currency" -> JsString("AUD"),
              "value" -> JsNumber(877124)
            )
          )
        }
      }
    }

    describe(".read") {
      describe("with a valid JSON value") {
        val json = """{ "currency": "USD", "value": 4891551 }""".parseJson

        it("should properly deserialize the value") {
          val money = json.convertTo[Money]

          money should be (Money.of(USD, 48915.51))
        }
      }

      describe("With a string for value") {
        val json = """{ "currency": "USD", "value": "123456" }""".parseJson

        it("should throw a deserialization exception") {
          assertThrows[DeserializationException] {
            json.convertTo[Money]
          }
        }
      }
    }
  }
}
