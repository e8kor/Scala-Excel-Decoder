package excel.decoder

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.scalatest.{ FlatSpec, GivenWhenThen, Matchers }

class DecoderSpec extends FlatSpec with GivenWhenThen with Matchers {

  trait CellFixture {

    val cell: Cell = new XSSFWorkbook().createSheet().createRow(0).createCell(0)

  }

  case class IntegerValue(value: Int)

  "Decoder" should "allow to map parsed things" in new CellFixture {
    Given("integer cell")
    cell.setCellValue(100)
    When("decoder and map result")
    val result = implicits.intCD.map(IntegerValue.apply)(cell)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("value should match")
    result shouldBe Right(IntegerValue(cell.getNumericCellValue.toInt))
  }

  "Decoder" should "allow to map parsed things, but keep errors" in new CellFixture {
    Given("integer cell")
    cell.setCellValue(100.2)
    When("decoder and map result")
    val result = implicits.intCD.map(IntegerValue.apply)(cell)
    Then("error occur")
    result shouldBe a[Left[_, _]]
  }
}
