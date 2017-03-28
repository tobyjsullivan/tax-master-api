package server.handlers.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import invoices.{Client, Invoice}
import org.joda.money.{CurrencyUnit, Money}
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.LocalDate
import spray.json._

import scala.util.{Failure, Success, Try}

object InvoiceProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val clientFormat = jsonFormat1(Client)
  implicit val invoiceFormat = jsonFormat3(Invoice)

  implicit object LocalDateFormat extends RootJsonFormat[LocalDate] {
    val dateFormat = ISODateTimeFormat.date()

    def write(d: LocalDate) =
      JsString(dateFormat.print(d))

    def read(json: JsValue) = json match {
      case JsString(s) =>
        Try(dateFormat.parseLocalDate(s)) match {
          case Success(date) => date
          case Failure(e) => deserializationError("Unrecognized date format.", e)
        }
      case _ => deserializationError("Expected date string")
    }
  }

  implicit object MoneyFormat extends RootJsonFormat[Money] {
    def write(m: Money) =
      JsObject(
        "value" -> JsNumber(m.getAmountMinor.longValue()),
        "currency" -> JsString(m.getCurrencyUnit.getCode)
      )

    def read(json: JsValue) = json.asJsObject.getFields("value", "currency") match {
      case Seq(JsNumber(value), JsString(currencyCode)) =>
        Try(CurrencyUnit.of(currencyCode))
          .flatMap(cur => Try(Money.ofMinor(cur, value.longValue()))) match {
          case Success(m) => m
          case Failure(e) => deserializationError("Unrecognized money format.", e)
        }
      case _ => deserializationError("Expected money format.")
    }
  }
}
