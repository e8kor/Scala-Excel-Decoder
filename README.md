# Scala Excel Decoder

## HOW TO

```scala
import cats.implicits._
import excel.address.SheetAddress
import excel.book.Book
import excel.decoder.implicits._
import excel.decoder.row.RowDecoder
import excel.ops._
import org.apache.poi.ss.usermodel.Workbook

case class Employee(id: Int, name: String, surname: String, title: String)

object Employee {

  implicit val dec: RowDecoder[Employee] = product4[Int, String, String, String].map((Employee.apply _).tupled)

}

for {
 book <- Book("example.xlsx")
 items <- book[Employee](SheetAddress("Employees"))
} yield items

```