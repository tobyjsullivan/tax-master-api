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
  private val dynamoDbClient = AmazonDynamoDBClientBuilder.standard()
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
    val Currency = "Currency"
    val Amount = "Amount"
  }

  private val dateFmt = ISODateTimeFormat.date()

  override def getAllInvoices(): InvoiceCollection = {
    val fields = new java.util.ArrayList[String]()
    fields.add(InvoiceFields.ID)
    fields.add(InvoiceFields.ClientID)
    fields.add(InvoiceFields.IssueDate)
    fields.add(InvoiceFields.Currency)
    fields.add(InvoiceFields.Amount)

    val items: Seq[mutable.Map[String, AttributeValue]] = dynamoDbClient.scan(Tables.INVOICES, fields).getItems
      .asScala.map(_.asScala)
    val invoices: Seq[Invoice] = items.map { m =>
      val id: String = m(InvoiceFields.ID).getS
      val clientId: String = m(InvoiceFields.ClientID).getS
      val issueDate: LocalDate = dateFmt.parseLocalDate(m(InvoiceFields.IssueDate).getS)
      val currency: String = m(InvoiceFields.Currency).getS
      val amount: Long = java.lang.Long.parseLong(m(InvoiceFields.Amount).getN)

      Invoice(
        id = Some(id),
        client = getClient(clientId).get,
        issueDate,
        amount = Money.ofMinor(CurrencyUnit.getInstance(currency), amount)
      )
    }.seq

    InvoiceCollection(invoices)
  }

  private def getClient(id: String): Option[Client] = {
    val key = Map(
      ClientFields.ID -> new AttributeValue(id)
    )

    val res = dynamoDbClient.getItem(Tables.CLIENTS, key.asJava)

    val fields = res.getItem.asScala

    fields.get(ClientFields.Name).map { name =>
      Client(Some(id), name.getS)
    }
  }

  override def getInvoice(id: String): Option[Invoice] = ???

  override def saveInvoice(invoice: Invoice): Unit = {
    saveClient(invoice.client)

    val table = Tables.INVOICES

    val item: Map[String, AttributeValue] = Map(
      InvoiceFields.ID -> new AttributeValue(invoice.id.get),
      InvoiceFields.ClientID -> new AttributeValue(invoice.client.id.get),
      InvoiceFields.IssueDate -> new AttributeValue(dateFmt.print(invoice.issueDate)),
      InvoiceFields.Currency -> new AttributeValue(invoice.amount.getCurrencyUnit.getCurrencyCode),
      InvoiceFields.Amount -> new AttributeValue().withN(invoice.amount.getAmountMinorLong.toString)
    )

    dynamoDbClient.putItem(table, item.asJava)
  }

  private def saveClient(client: Client): Unit = {
    val table = Tables.CLIENTS

    val item: Map[String, AttributeValue] = Map(
      ClientFields.ID -> new AttributeValue(client.id.get),
      ClientFields.Name -> new AttributeValue(client.name)
    )

    dynamoDbClient.putItem(table, item.asJava)
  }
}
