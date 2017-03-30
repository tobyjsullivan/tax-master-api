package handlers.json

import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import spray.json._

import scala.util.{Failure, Success, Try}

private[json] trait LocalDateProtocol {
  implicit object LocalDateFormat extends RootJsonFormat[LocalDate] {
    private val dateFormat = ISODateTimeFormat.date()

    def write(d: LocalDate) =
      JsString(dateFormat.print(d))

    def read(json: JsValue): LocalDate = json match {
      case JsString(s) =>
        Try(dateFormat.parseLocalDate(s)) match {
          case Success(date) => date
          case Failure(e) => deserializationError("Unrecognized date format.", e)
        }
      case _ => deserializationError("Expected date string")
    }
  }
}
