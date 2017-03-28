package invoices

import org.joda.money.Money
import org.joda.money.CurrencyUnit._
import org.joda.time.LocalDate

object InvoiceStore {
  def getAll(): Seq[Invoice] =
    Seq(
      Invoice(Client("Globex"), new LocalDate(2017, 3, 11), Money.of(USD, 1234.56)),
      Invoice(Client("Umbrella Corp."), new LocalDate(2017, 2, 25), Money.of(CAD, 2452.12))
    )
}
