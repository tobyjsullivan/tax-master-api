package invoices

import org.joda.money.Money
import org.joda.money.CurrencyUnit._
import org.joda.time.LocalDate

object InvoiceStore {
  def getAll(): Seq[Invoice] =
    Seq(
      Invoice(
        "6929ac16-cf62-4ef0-a3bd-2e9a350df6fb",
        Client(
          "0514c037-4505-4e7d-9732-94515c938087",
          "Globex"
        ),
        new LocalDate(2017, 3, 11),
        Money.of(USD, 1234.56)
      ),
      Invoice(
        "d124ced1-c4ac-4ccd-89df-ea57ccc52017",
        Client(
          "a96fe6d8-c2d0-4efd-8d65-4d34bf474635",
          "Umbrella Corp."
        ),
        new LocalDate(2017, 2, 25),
        Money.of(CAD, 2452.12)
      )
    )
}
