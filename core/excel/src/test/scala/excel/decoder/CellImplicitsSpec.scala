package excel.decoder

import excel.exceptions._
import java.util.Date
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.scalatest.{ FlatSpec, GivenWhenThen, Matchers }

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
    result shouldBe a[Right[_, _]]
    And("cell value and decoded value should match")
    result should equal(Right(cell.getStringCellValue))
  }

  it should "decode cell value of non string cell" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue(1.2)
    When("decode value")
    val result = implicits.stringCD.decode(cell)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("cell value and decoded value should match")
    result should equal(Right(cell.getNumericCellValue.toString))
  }

  "Integer Cell Decoder" should "decode cell value" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue(1)
    When("decode value")
    val result = implicits.intCD.decode(cell)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("cell value and decoded value should match")
    result should equal(Right(cell.getNumericCellValue.intValue()))
  }

  it should "not decode cell value" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue("1")
    When("decode value")
    val result = implicits.intCD.decode(cell)
    Then("error occur")
    result shouldBe a[Left[_, _]]
  }

  "Double Cell Decoder" should "decode cell value" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue(1.2D)
    When("decode value")
    val result = implicits.doubleCD.decode(cell)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("cell value and decoded value should match")
    result should equal(Right(cell.getNumericCellValue))
  }

  it should "not decode cell value" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue("1.2D")
    When("decode value")
    val result = implicits.doubleCD.decode(cell)
    Then("error occur")
    result shouldBe a[Left[_, _]]
  }

  "Date Cell Decoder" should "decode cell value" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue(new Date())
    When("decode value")
    val result = implicits.dateTimeCD.decode(cell)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("cell value and decoded value should match")
    result should equal(Right(cell.getDateCellValue))
  }

  it should "not decode cell value" in new CellFixture {
    Given("cell testificant")
    cell.setCellValue("1.2D")
    When("decode value")
    val result = implicits.dateTimeCD.decode(cell)
    Then("error occur")
    result shouldBe a[Left[_, _]]
  }

  "Option Decoder" should "not decode cell value" in new CellFixture {
    Given("cell testificant")
    //cell
    When("decode value")
    val result = implicits.optionCD[Date](implicits.dateTimeCD).decode(cell)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("decoded value should be None")
    result should equal(Right(None))
  }

}