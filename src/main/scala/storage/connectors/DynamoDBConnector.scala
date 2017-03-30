package storage.connectors
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import models.invoices.{Client, Invoice, InvoiceCollection}
import org.joda.money.{CurrencyUnit, Money}
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat

import scala.collection.mutable
import scala.collection.JavaConverters._

object DynamoDBConnector extends InvoiceConnector {
  private val client = AmazonDynamoDBClientBuilder.standard()
    .withRegion(Regions.US_WEST_2)
    .withCredentials(new ProfileCredentialsProvider())
    .build()

  private object Tables {
    val CLIENTS = "tm-clients"
    val INVOICES = "tm-invoices"
  }

  private object ClientFields {
    val ID = "ID"
    val Name = "Name"
  }

  private object InvoiceFields {
    val ID = "ID"
    val ClientID = "Client ID"
    val IssueDate = "Issue Date"
  }

  private val dateFmt = ISODateTimeFormat.date()

  override def getAllInvoices(): InvoiceCollection = {
    val fields = new java.util.ArrayList[String]()
    fields.add(InvoiceFields.ID)
    fields.add(InvoiceFields.ClientID)
    fields.add(InvoiceFields.IssueDate)

    val items: Seq[mutable.Map[String, AttributeValue]] = client.scan(Tables.INVOICES, fields).getItems
      .asScala.map(_.asScala)
    val invoices: Seq[Invoice] = items.map { m =>
      val id: String = m(InvoiceFields.ID).getS
      val clientId: String = m(InvoiceFields.ClientID).getS
      val issueDate: LocalDate = dateFmt.parseLocalDate(m(InvoiceFields.IssueDate).getS)

      // TODO Load client name and amount
      Invoice(id = id, Client(id = clientId, name = "TODO"), issueDate, amount = Money.of(CurrencyUnit.CAD, 100.0))
    }.seq

    InvoiceCollection(invoices)
  }

  override def getInvoice(id: String): Option[Invoice] = ???

  override def saveInvoice(invoice: String): Unit = ???
}
