package storage

import org.scalatest._
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.pattern.ask
import akka.util.Timeout
import models.invoices.{Client, Invoice, InvoiceCollection}
import org.joda.money.CurrencyUnit.{CAD, USD}
import org.joda.money.{CurrencyUnit, Money}
import org.joda.time.LocalDate
import storage.connectors.InvoiceConnector

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class InvoiceStoreSpec extends TestKit(ActorSystem("TestSystem"))
  with ImplicitSender
  with FunSpecLike
  with Matchers
  with BeforeAndAfterAll {
  private val testInvoice0 = Invoice(
    Some("6929ac16-cf62-4ef0-a3bd-2e9a350df6fb"),
    Client(
      Some("0514c037-4505-4e7d-9732-94515c938087"),
      "Globex"
    ),
    new LocalDate(2017, 3, 11),
    Money.of(USD, 1234.56)
  )
  private val testInvoice1 = Invoice(
    Some("d124ced1-c4ac-4ccd-89df-ea57ccc52017"),
    Client(
      Some("a96fe6d8-c2d0-4efd-8d65-4d34bf474635"),
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
      case "6929ac16-cf62-4ef0-a3bd-2e9a350df6fb" => Some(testInvoice0)
      case "d124ced1-c4ac-4ccd-89df-ea57ccc52017" => Some(testInvoice1)
      case _ => None
    }

    private var lastSavedInvoice: Option[Invoice] = None
    override def saveInvoice(invoice: Invoice): Unit = {
      lastSavedInvoice = Some(invoice)
    }

    def resetSavedInvoice(): Unit = {
      lastSavedInvoice = None
    }

    def savedInvoice: Option[Invoice] = lastSavedInvoice
  }

  override def afterAll(): Unit = shutdown(system)

  describe("InvoiceStore") {
    val storeRef = system.actorOf(Props(new InvoiceStore(testConnector)))
    implicit val askTimeout = Timeout(100 milliseconds)

    describe(".tell") {
      describe("when sent a GetInvoices request") {
        it("should respond with list of models.invoices") {
          storeRef.tell(InvoiceStore.GetInvoices, testActor)

          expectMsg[InvoiceCollection](
            InvoiceCollection(Seq(testInvoice0, testInvoice1))
          )
        }
      }

      describe("when sent a GetInvoice request") {
        describe("with a valid invoice ID") {
          val invoiceId = testInvoice1.id.get
          it("should return the invoice") {
            storeRef.tell(InvoiceStore.GetInvoice(invoiceId), testActor)

            expectMsg(Some(testInvoice1))
          }
        }

        describe("with a non-existant invoice ID") {
          val invoiceId = "6ee432d6-2ee6-4ea0-b85d-d4a52fcf9de3"

          it("should return None") {
            storeRef.tell(InvoiceStore.GetInvoice(invoiceId), testActor)

            expectMsg(None)
          }
        }

      }

      describe("when sent a SaveInvoice message") {
        describe("with a valid invoice") {
          val invoice = Invoice(
            id = None,
            client = Client(
              id = None,
              name = "Acme Corp."
            ),
            issueDate = new LocalDate(2017, 7, 21),
            amount = Money.of(CurrencyUnit.EUR, 6982.56)
          )

          it("should save that invoice to the connector") {
            testConnector.resetSavedInvoice()
            storeRef.tell(InvoiceStore.SaveInvoice(invoice), testActor)

            // Wait for the save confirmation
            expectMsgPF() {
              case InvoiceStore.InvoiceSaved(Invoice(Some(_), Client(Some(_), _), _, _)) => true
            }

            val saved = testConnector.savedInvoice.get

            saved.id shouldBe a [Some[_]]
            saved.client.id shouldBe a [Some[_]]
            saved.client.name should be (invoice.client.name)
            saved.issueDate should be (invoice.issueDate)
            saved.amount should be (invoice.amount)
          }
        }
      }
    }
  }
}
