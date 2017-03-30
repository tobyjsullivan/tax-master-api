package handlers.json

import org.joda.money.{CurrencyUnit, Money}
import spray.json._

import scala.util.{Failure, Success, Try}

private[json] trait MoneyProtocol {
  implicit object MoneyFormat extends RootJsonFormat[Money] {
    def write(m: Money) =
      JsObject(
        "value" -> JsNumber(m.getAmountMinor.longValue()),
        "currency" -> JsString(m.getCurrencyUnit.getCode)
      )

    def read(json: JsValue): Money = json.asJsObject.getFields("value", "currency") match {
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
