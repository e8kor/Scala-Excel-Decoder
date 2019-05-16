package excel.decoder

import excel.exceptions.ParseError
import java.util.Date
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.scalatest.{ FlatSpec, FreeSpec, FunSpec, GivenWhenThen, Matchers }

class CellImplicitsSpec extends FlatSpec with GivenWhenThen with Matchers {

  trait CellFixture {

    val cell: Cell = new XSSFWorkbook().createSheet().createRow(0).createCell(0)

  }

  "String Cell Decoder" should "decode cell value" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue("1")
    When("decode value")
    val result = implicits.stringCD.decode(cell)
    Then("no error occur")
    result shouldBe a[Right[ParseError, String]]
    And("cell value and decoded value should match")
    result should equal(Right(cell.getStringCellValue))
  }

  it should "decode cell value of non string cell" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue(1.2)
    When("decode value")
    val result = implicits.stringCD.decode(cell)
    Then("no error occur")
    result shouldBe a[Right[ParseError, String]]
    And("cell value and decoded value should match")
    result should equal(Right(cell.getNumericCellValue.toString))
  }

  "Integer Cell Decoder" should "decode cell value" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue(1)
    When("decode value")
    val result = implicits.intCD.decode(cell)
    Then("no error occur")
    result shouldBe a[Right[ParseError, Int]]
    And("cell value and decoded value should match")
    result should equal(Right(cell.getNumericCellValue.intValue()))
  }

  it should "not decode cell value" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue("1")
    When("decode value")
    val result = implicits.intCD.decode(cell)
    Then("error occur")
    result shouldBe a[Left[ParseError, Int]]
  }

  "Double Cell Decoder" should "decode cell value" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue(1.2D)
    When("decode value")
    val result = implicits.doubleCD.decode(cell)
    Then("no error occur")
    result shouldBe a[Right[ParseError, Double]]
    And("cell value and decoded value should match")
    result should equal(Right(cell.getNumericCellValue))
  }

  it should "not decode cell value" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue("1.2D")
    When("decode value")
    val result = implicits.doubleCD.decode(cell)
    Then("error occur")
    result shouldBe a[Left[ParseError, Double]]
  }

  "Date Cell Decoder" should "decode cell value" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue(new Date())
    When("decode value")
    val result = implicits.dateTimeCD.decode(cell)
    Then("no error occur")
    result shouldBe a[Right[ParseError, Date]]
    And("cell value and decoded value should match")
    result should equal(Right(cell.getDateCellValue))
  }

  it should "not decode cell value" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue("1.2D")
    When("decode value")
    val result = implicits.dateTimeCD.decode(cell)
    Then("error occur")
    result shouldBe a[Left[ParseError, Date]]
  }

  "Option Decoder" should "not decode cell value" in new CellFixture {
    Given("cell testificant")
    cell
    When("decode value")
    val result = implicits.optionCD[Date](implicits.dateTimeCD).decode(cell)
    Then("no error occur")
    result shouldBe a[Right[ParseError, Option[Date]]]
    And("decoded value should be None")
    result should equal(Right(None))
  }

}
