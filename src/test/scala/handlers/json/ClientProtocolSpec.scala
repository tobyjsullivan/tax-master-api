package handlers.json

import models.invoices.Client
import org.scalatest._
import spray.json._

class ClientProtocolSpec extends FunSpec with Matchers with ClientProtocol {
  describe("clientFormat") {
    describe(".write") {
      describe("with a complete Client") {
        val client = Client(
          id = Some("e99b6f5e-a312-47a6-8a0c-4e5588458bec"),
          name = "Globex Corp."
        )
        it("should serialize to json") {
          val js = client.toJson

          js should be (JsObject(
            "id" -> JsString("e99b6f5e-a312-47a6-8a0c-4e5588458bec"),
            "name" -> JsString("Globex Corp.")
          ))
        }
      }
    }

    describe(".read") {
      describe("with a valid JSON value") {
        val json = """{ "id": "2ea297fd-7812-4efd-80c2-e0af93fc2e8a", "name": "Umbrella Corp." }""".parseJson

        it("should unmarshall the correct Client") {
          val client: Client = json.convertTo[Client]

          client.id should be (Some("2ea297fd-7812-4efd-80c2-e0af93fc2e8a"))
          client.name should be ("Umbrella Corp.")
        }
      }

      describe("when id field is missing") {
        val json = """{ "name": "Globex" }""".parseJson

        it("should unmarshall a Client with no ID") {
          val client: Client = json.convertTo[Client]

          client should be (Client(id = None, name = "Globex"))
        }
      }

      describe("when name field is missing") {
        val json = """{ "id": "87c4dd6b-2ec6-4457-96ca-578791a994fb" }""".parseJson

        it ("should throw a DeserializationException") {
          assertThrows[DeserializationException] {
            json.convertTo[Client]
          }
        }
      }
    }
  }
}
