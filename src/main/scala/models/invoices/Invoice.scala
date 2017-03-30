package models.invoices

import org.joda.money.Money
import org.joda.time.LocalDate

case class Invoice(id: Option[String], client: Client, issueDate: LocalDate, amount: Money)
