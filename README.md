# Scala Excel Decoder

## HOW TO

```scala
import cats.implicits._
import excel.address.SheetAddress
import excel.book.Book
import excel.decoder.RowDecoder
import excel.decoder.implicits._
import excel.ops._
import org.apache.poi.ss.usermodel.Workbook

case class Employee(id: Int, name: String, surname: String, title: String)

object Employee {
  implicit val dec: RowDecoder[Employee] = Employee.apply _
}

for {
 book <- Book("example.xlsx")
 items <- book[Employee](SheetAddress("Employees"))
} yield items

for {
 book <- Book("example.xlsx")
 items <- book[Employee](AreaAddress("Sheet2", 2, 1, 4, 4))
} yield items

```