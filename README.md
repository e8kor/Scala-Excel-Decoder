# Scala Excel Decoder [ ![Download](https://api.bintray.com/packages/e8kor/maven/excel/images/download.svg?version=0.0.1) ](https://bintray.com/e8kor/maven/excel/0.0.1/link)

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