package excel.address

import excel.book.Book
import org.apache.poi.ss.usermodel._
import org.scalatest.{ FlatSpec, GivenWhenThen, Matchers }

class AddressSpec extends FlatSpec with GivenWhenThen with Matchers {

  trait Fixture {

    val book: Workbook = Book("AddressSpec.xlsx").right.get

  }

  "SheetAddress" should "use sheet name and return all rows on page " in new Fixture {
    Given("Address")
    val address = SheetAddress("Sheet Case")
    When("parse rows")
    val rows = address.rows(book)
    Then("no error occur")
    rows shouldBe a[Right[_, _]]
    And("expected rowcount")
    rows.right.get should have size 3
  }

  it should "return error sheet not exists" in new Fixture {
    Given("Address")
    val address1 = SheetAddress("non existing sheet")
    val address2 = SheetAddress(100)
    When("parse rows")
    val rows1 = address1.rows(book)
    val rows2 = address2.rows(book)
    Then("error occur")
    rows1 shouldBe a[Left[_, _]]
    rows2 shouldBe a[Left[_, _]]
  }

  it should "use sheet index and return all rows on page " in new Fixture {
    Given("Address")
    val address = SheetAddress(1)
    When("parse rows")
    val rows = address.rows(book)
    Then("no error occur")
    rows shouldBe a[Right[_, _]]
    And("expected rowcount")
    rows.right.get should have size 3
  }

  "AreaAddress" should "use formula and return all rows within formula area" in new Fixture {
    Given("Address")
    val address = AreaAddress("Employees")
    When("parse rows")
    val rows = address.rows(book)
    Then("no error occur")
    rows shouldBe a[Right[_, _]]
    And("expected rowcount")
    rows.right.get should have size 3
  }
  it should "use formula and return error if not exists" in new Fixture {
    Given("Address")
    val address = AreaAddress("No Formula")
    When("parse rows")
    val rows = address.rows(book)
    Then("error occur")
    rows shouldBe a[Left[_, _]]
  }

  it should "use sheet name, start and stop cell coords and return all rows within designated area" in new Fixture {
    Given("Address")
    val address = AreaAddress("Coordinate Case", 1, 3, 4, 7)
    When("parse rows")
    val rows = address.rows(book)
    Then("no error occur")
    rows shouldBe a[Right[_, _]]
    And("expected rowcount")
    rows.right.get should have size 4
  }

}
