package excel

import excel.address._
import excel.book.Book
import excel.decoder.RowDecoder
import excel.decoder.implicits._
import excel.ops._
import org.apache.poi.ss.usermodel.Workbook
import org.scalatest._

class UsageSpec extends FlatSpec with GivenWhenThen with Matchers {

  case class Employee(id: Int, name: String, surname: String, title: String)

  object Employee {

    implicit val dec: RowDecoder[Employee] = Employee.apply _

  }

  trait Fixture {
    val book: Workbook = Book("UsageSpec.xlsx").right.get
  }

  "Example 1" should "decode entities from workbook using sheet name" in new Fixture {
    book
      .apply[Employee](AddressBuilder.sheet("Employees").build())
      .shouldBe(
        Right(
          List(
            Employee(1, "Peter", "Pew", "Developer"),
            Employee(2, "Pew", "Pew", "Quality Assurance")
          )))
  }

  "Example 1" should "decode entities from workbook using sheet name and coordinates" in new Fixture {
    book
      .apply[Employee](AddressBuilder.area("Sheet2")(1 -> 2, 4 -> 4).build())
      .shouldBe(
        Right(
          List(
            Employee(1, "Peter", "Pew", "Developer"),
            Employee(2, "Pew", "Pew", "Quality Assurance"),
            Employee(3, "Pew", "Die", "Director")
          )))
  }
}
