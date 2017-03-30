package handlers

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model._
import akka.testkit._
import org.scalatest._

class IndexHandlerSpec extends TestKit(ActorSystem("TestSystem"))
  with ImplicitSender
  with FunSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = shutdown(system)

  describe("IndexHandler") {
    val handlerRef = system.actorOf(Props[IndexHandler])

    describe(".tell") {
      describe("when sent a GET request for the root path") {
        it("should respond with a 200 OK") {
          handlerRef.tell(HttpRequest(HttpMethods.GET, "/"), testActor)

          expectMsgPF() {
            case HttpResponse(StatusCodes.OK, _, _, _) => true
          }
        }
      }
    }
  }
}
