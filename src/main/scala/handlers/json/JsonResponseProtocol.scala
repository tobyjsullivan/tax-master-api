package handlers.json

import handlers.JsonResponse
import spray.json._

private[json] trait JsonResponseProtocol extends DefaultJsonProtocol {
  implicit def jsonResponseFormat[A :JsonFormat] = jsonFormat1(JsonResponse.apply[A])

}
