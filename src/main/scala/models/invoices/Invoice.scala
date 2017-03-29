package models.invoices

import org.joda.money.Money
import org.joda.time.LocalDate

case class Invoice(id: String, client: Client, issueDate: LocalDate, amount: Money)
