package excel.spec

import excel.address.SheetAddress
import excel.book.Book
import excel.decoder.implicits._
import excel.decoder.row.RowDecoder
import excel.ops._
import org.apache.poi.ss.usermodel.Workbook
import org.scalatest._

case class Employee(id: Int, name: String, surname: String, title: String)

object Employee {

  implicit val dec: RowDecoder[Employee] = Employee.apply _

}

class WorkbookSpec extends FreeSpec with Matchers {

  val example: Workbook = Book("example.xlsx").right.get

  "Reader" - {
    "case class" in {
      example
        .apply[Employee](SheetAddress("Employees"))
        .shouldBe(
          Right(
            List(
              Employee(1, "Peter", "Pew", "Developer"),
              Employee(2, "Pew", "Pew", "Quality Assurance")
            )))
    }
  }
}
