package invoices

import org.joda.money.Money
import org.joda.time.LocalDate

case class Invoice(client: Client, issueDate: LocalDate, amount: Money)
