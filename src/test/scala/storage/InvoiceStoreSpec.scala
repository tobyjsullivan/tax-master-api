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

import scala.concurrent.duration._
import scala.util.{Failure, Success}

class InvoiceStoreSpec extends TestKit(ActorSystem("TestSystem"))
  with ImplicitSender
  with AsyncFunSpecLike
  with Matchers
  with BeforeAndAfterAll {
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

  override def afterAll(): Unit = shutdown(system)

  describe("InvoiceStore") {
    val storeRef = system.actorOf(Props(new InvoiceStore(testConnector)))
    implicit val askTimeout = Timeout(100 milliseconds)

    describe(".receive") {
      describe("when sent a GetInvoices request") {
        it("should respond with list of models.invoices") {
          val fInvoices = (storeRef ? InvoiceStore.GetInvoices).mapTo[InvoiceCollection]

          fInvoices.map { invoices =>
            invoices should be (
              InvoiceCollection(
                Seq(
                  testInvoice0,
                  testInvoice1
                )
              )
            )
          }
        }
      }

      describe("when sent a GetInvoice request") {
        describe("with a valid invoice ID") {
          val invoiceId = testInvoice1.id
          it("should return the invoice") {
            val fInvoice = (storeRef ? InvoiceStore.GetInvoice(invoiceId)).mapTo[Option[Invoice]]

            fInvoice.map { oInvoice =>
              oInvoice should be (Some(testInvoice1))
            }
          }
        }

        describe("with a non-existant invoice ID") {
          val invoiceId = "6ee432d6-2ee6-4ea0-b85d-d4a52fcf9de3"

          it("should return None") {
            val fInvoice = (storeRef ? InvoiceStore.GetInvoice(invoiceId)).mapTo[Option[Invoice]]

            fInvoice.map { oInvoice =>
              oInvoice should be (None)
            }
          }
        }

      }
    }
  }
}
