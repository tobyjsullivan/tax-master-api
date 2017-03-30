package handlers

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import models.invoices.{Client, Invoice, InvoiceCollection}
import org.joda.money.CurrencyUnit.{CAD, USD}
import org.joda.money.Money
import org.joda.time.LocalDate
import org.scalatest.{BeforeAndAfterAll, FunSpecLike, Matchers}
import spray.json._
import storage.connectors.InvoiceConnector

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class InvoicesHandlerSpec extends TestKit(ActorSystem("TestSystem"))
  with ImplicitSender
  with FunSpecLike
  with Matchers
  with BeforeAndAfterAll {
  implicit val materializer = ActorMaterializer()

  override def afterAll(): Unit = shutdown(system)

  private val testInvoice0 = Invoice(
    "6929ac16-cf62-4ef0-a3bd-2e9a350df6fb",
    Client(
      "0514c037-4505-4e7d-9732-94515c938087",
      "Globex"
    ),
    new LocalDate(2017, 3, 11),
    Money.of(USD, 1234.56)
  )
  private val testInvoice1 = Invoice(
    "d124ced1-c4ac-4ccd-89df-ea57ccc52017",
    Client(
      "a96fe6d8-c2d0-4efd-8d65-4d34bf474635",
      "Umbrella Corp."
    ),
    new LocalDate(2017, 2, 25),
    Money.of(CAD, 2452.12)
  )

  private object testConnector extends InvoiceConnector {
    override def getAllInvoices(): InvoiceCollection =
      InvoiceCollection(
        Seq(
          testInvoice0,
          testInvoice1
        )
      )

    override def getInvoice(id: String): Option[Invoice] = id match {
      case testInvoice0.id => Some(testInvoice0)
      case testInvoice1.id => Some(testInvoice1)
      case _ => None
    }

    override def saveInvoice(invoice: String): Unit = ???
  }

  describe("InvoicesHandler") {
    val handlerRef = system.actorOf(Props(new InvoicesHandler(testConnector)))

    describe("when sent a GET request to /invoices") {
      it("should return a JSON list of invoices") {
        handlerRef ! HttpRequest(HttpMethods.GET, "/invoices")

        val entity = expectMsgPF() {
          case HttpResponse(StatusCodes.OK, _, entity, _) => entity
        }

        entity.getContentType() should be (ContentTypes.`application/json`)

        val json = Await.result(entity.toStrict(100 milliseconds).map(_.data.utf8String), 100 milliseconds).parseJson

        json shouldBe a [JsObject]
        val payload = json.asJsObject.getFields("payload").head

        payload shouldBe a [JsArray]
        payload.asInstanceOf[JsArray].elements.length should be (2)
      }
    }
  }
}
