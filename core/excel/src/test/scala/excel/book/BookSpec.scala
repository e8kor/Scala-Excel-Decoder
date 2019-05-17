package excel.book

import org.scalatest.{ FlatSpec, GivenWhenThen, Matchers }
import scala.reflect.io.Path

class BookSpec extends FlatSpec with GivenWhenThen with Matchers {

  "Book" should "be created if file exists in resources" in {
    Given("resource path")
    val path = "UsageSpec.xlsx"
    When("Read file")
    val book = Book(path)
    Then("no error occur")
    book shouldBe a[Right[_, _]]
  }

  "Book" should "be created if file exists at path" in {
    Given("file path")
    val path = Path("./core/excel/src/test/resources/UsageSpec.xlsx")
    When("Read file")
    val book = Book(path)
    Then("no error occur")
    book shouldBe a[Right[_, _]]
  }

  "Book" should "return error if file not exists at path" in {
    Given("file path")
    val path = Path("./core/excel/src/test/resources/NotExists.xlsx")
    When("Read file")
    val book = Book(path)
    Then("error occur")
    book shouldBe a[Left[_, _]]
  }
  "Book" should "return error if resorce not exists" in {
    Given("resource name")
    val path = Path("NotExists.xlsx")
    When("Read file")
    val book = Book(path)
    Then("error occur")
    book shouldBe a[Left[_, _]]
  }

  "Book" should "return error if file is not excel" in {
    Given("file path")
    val path = Path("./core/excel/src/test/resources/NotWorkbook.txt")
    When("Read file")
    val book = Book(path)
    Then("error occur")
    book shouldBe a[Left[_, _]]
  }

}
