package handlers.json

import models.invoices.Client
import spray.json._

private[json] trait ClientProtocol extends DefaultJsonProtocol with NullOptions {
  implicit val clientFormat: RootJsonFormat[Client] = jsonFormat2(Client)
}
