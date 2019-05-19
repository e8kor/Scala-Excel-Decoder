package excel.decoder

import com.typesafe.scalalogging.LazyLogging
import java.util.Date
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.scalatest.{ FlatSpec, GivenWhenThen, Matchers }
import scala.collection.mutable

class CellImplicitsSpec extends FlatSpec with GivenWhenThen with Matchers {

  trait CellFixture extends LazyLogging {

    private var columnIndex = 0
    private val rowInstance: mutable.ListBuffer[Cell] = mutable.ListBuffer.empty

    def withCell(f: Cell => Unit): Unit = {
      val cell: Cell = new XSSFWorkbook().createSheet().createRow(0).createCell(columnIndex)
      columnIndex += 1
      f(cell)
      rowInstance.append(cell)
    }

    def cell(index: Int): Cell = {
      rowInstance(index)
    }

    def row: List[Cell] = rowInstance.toList

  }

  "String Cell Decoder" should "decode cell value" in new CellFixture {
    Given("cell testificant")
    withCell(_.setCellValue("1"))
    When("decode value")
    val result = implicits.stringCD.decode(row)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("cell value and decoded value should match")
    result should equal(Right((Nil, cell(0).getStringCellValue)))
  }

  it should "decode cell value of double cell" in new CellFixture {
    Given("cell testificant")
    withCell(_.setCellValue(1.2))
    When("decode value")
    val result = implicits.stringCD.decode(row)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("cell value and decoded value should match")
    result should equal(Right((Nil, cell(0).getNumericCellValue.toString)))
  }

  it should "decode cell value of boolean cell" in new CellFixture {
    Given("cell testificant")
    withCell(_.setCellValue(true))
    When("decode value")
    val result = implicits.stringCD.decode(row)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("cell value and decoded value should match")
    result should equal(Right((Nil, cell(0).getBooleanCellValue.toString)))
  }

  it should "decode cell value of formula cell" in new CellFixture {
    Given("cell testificant")
    withCell(_.setCellFormula("SQRT(4)"))
    When("decode value")
    val result = implicits.stringCD.decode(row)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("cell value and decoded value should match")
    result should equal(Right((Nil, cell(0).getCellFormula)))
  }

  it should "not decode cell if no value setted" in new CellFixture {
    Given("cell testificant")
    withCell(_ => ())
    When("decode value")
    val result = implicits.stringCD.decode(row)
    Then("no error occur")
    result shouldBe a[Left[_, _]]
  }

  "Integer Cell Decoder" should "decode cell value" in new CellFixture {
    Given("cell testificant")
    withCell(_.setCellValue(1))
    When("decode value")
    val result = implicits.intCD.decode(row)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("cell value and decoded value should match")
    result should equal(Right((Nil, cell(0).getNumericCellValue.intValue())))
  }

  it should "not decode cell value if not integer number" in new CellFixture {
    Given("cell testificant")
    withCell(_.setCellValue(1.2))
    When("decode value")
    val result = implicits.intCD.decode(row)
    Then("error occur")
    result shouldBe a[Left[_, _]]
  }

  it should "not decode cell value if not numeric" in new CellFixture {
    Given("cell testificant")
    withCell(_.setCellValue("1"))
    When("decode value")
    val result = implicits.intCD.decode(row)
    Then("error occur")
    result shouldBe a[Left[_, _]]
  }

  "Double Cell Decoder" should "decode cell value" in new CellFixture {
    Given("cell testificant")
    withCell(_.setCellValue(1.2D))
    When("decode value")
    val result = implicits.doubleCD.decode(row)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("cell value and decoded value should match")
    result should equal(Right((Nil, cell(0).getNumericCellValue)))
  }

  it should "not decode cell value" in new CellFixture {
    Given("cell testificant")
    withCell(_.setCellValue("1.2D"))
    When("decode value")
    val result = implicits.doubleCD.decode(row)
    Then("error occur")
    result shouldBe a[Left[_, _]]
  }

  "Date Cell Decoder" should "decode cell value" in new CellFixture {
    Given("cell testificant")
    withCell(_.setCellValue(new Date()))
    When("decode value")
    val result = implicits.dateTimeCD.decode(row)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("cell value and decoded value should match")
    result should equal(Right((Nil, cell(0).getDateCellValue)))
  }

  it should "not decode cell value" in new CellFixture {
    Given("cell testificant")
    withCell(_.setCellValue("1.2D"))
    When("decode value")
    val result = implicits.dateTimeCD.decode(row)
    Then("error occur")
    result shouldBe a[Left[_, _]]
  }

  "Option Decoder" should "not decode cell value" in new CellFixture {
    Given("cell testificant")
    withCell(_ => ())
    When("decode value")
    val result = implicits.optionRD[Date](implicits.dateTimeCD).decode(row)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("decoded value should be None")
    result should equal(Right((Nil, None)))
  }

  "Option Decoder" should "decode cell value" in new CellFixture {
    Given("cell testificant")
    withCell(_.setCellValue("Foo"))
    When("decode value")
    val result = implicits.optionRD[String](implicits.stringCD).decode(row)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("decoded value should be None")
    result should equal(Right((Nil, Some(cell(0).getStringCellValue))))
  }

}
