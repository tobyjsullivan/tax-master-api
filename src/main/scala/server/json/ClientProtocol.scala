package server.json

import models.invoices.Client
import spray.json._

private[json] trait ClientProtocol extends DefaultJsonProtocol {
  implicit val clientFormat: RootJsonFormat[Client] = jsonFormat2(Client)
}
