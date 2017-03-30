package handlers.json

import org.scalatest._
import handlers.JsonResponse
import spray.json._

class JsonResponseProtocolSpec extends FunSpec with Matchers with JsonResponseProtocol {
  describe("jsonResponseFormat") {
    describe(".write") {
      describe("with a JsonResponse") {
        val resp = JsonResponse("Expected Content")

        it("should produce the expected JSON") {
          val json = resp.toJson

          json should be (
            JsObject(
              "payload" -> JsString("Expected Content")
            )
          )
        }
      }
    }
  }
}
