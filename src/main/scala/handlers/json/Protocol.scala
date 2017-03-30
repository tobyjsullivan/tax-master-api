package handlers.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

object Protocol extends SprayJsonSupport
  with DefaultJsonProtocol
  with ClientProtocol
  with LocalDateProtocol
  with MoneyProtocol
  with InvoiceProtocol
  with InvoiceCollectionProtocol
  with JsonResponseProtocol {

}
