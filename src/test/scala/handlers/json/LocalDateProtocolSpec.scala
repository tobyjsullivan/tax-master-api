package handlers.json

import org.joda.time.DateTimeConstants._
import org.joda.time.LocalDate
import org.scalatest._
import spray.json._

class LocalDateProtocolSpec extends FunSpec with Matchers with LocalDateProtocol {
  describe("localDateFormat") {
    describe(".write") {
      describe("with a LocalDate instance") {
        val date = new LocalDate(2016, 7, 24)

        it("should serialize to the expected date string") {
          val json = date.toJson

          json should be (JsString("2016-07-24"))
        }
      }
    }

    describe(".read") {
      describe("with a valid date string") {
        val js = "\"2017-02-22\"".parseJson

        it("should deserialize the correct local date") {
          val date = js.convertTo[LocalDate]

          date.getYear should be (2017)
          date.getMonthOfYear should be (FEBRUARY)
          date.getDayOfMonth should be (22)
        }
      }

      describe("with an invalid date string") {
        val js = "\"My Birthday\"".parseJson

        it("should throw a DeserializationException") {
          assertThrows[DeserializationException] {
            js.convertTo[LocalDate]
          }
        }
      }
    }
  }
}
