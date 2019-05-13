package excel.spec

import excel.address.{ AreaAddress, SheetAddress }
import excel.book.Book
import excel.decoder.RowDecoder
import excel.decoder.implicits._
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
    "case class on sheet" in {
      example
        .apply[Employee](SheetAddress("Employees"))
        .shouldBe(
          Right(
            List(
              Employee(1, "Peter", "Pew", "Developer"),
              Employee(2, "Pew", "Pew", "Quality Assurance")
            )))
    }

    "case class on area" in {
      example
        .apply[Employee](AreaAddress("Sheet2", 2, 1, 4, 4))
        .shouldBe(
          Right(
            List(
              Employee(1, "Peter", "Pew", "Developer"),
              Employee(2, "Pew", "Pew", "Quality Assurance"),
              Employee(3, "Pew", "Die", "Director")
            )))
    }
  }
}
