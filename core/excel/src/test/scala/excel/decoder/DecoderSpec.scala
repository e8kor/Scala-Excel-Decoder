package excel.decoder

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.scalatest.{ FlatSpec, GivenWhenThen, Matchers }
import scala.collection.mutable

class DecoderSpec extends FlatSpec with GivenWhenThen with Matchers {

  trait CellFixture {

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

  case class IntegerValue(value: Int)

  "Decoder" should "allow to map parsed things" in new CellFixture {
    Given("integer cell")
    withCell(_.setCellValue(100))
    When("decoder and map result")
    val result = implicits.intCD.map(x => x._1 -> IntegerValue(x._2))(row)
    Then("no error occur")
    result shouldBe a[Right[_, _]]
    And("value should match")
    result shouldBe Right((Nil, IntegerValue(cell(0).getNumericCellValue.toInt)))
  }

  "Decoder" should "allow to map parsed things, but keep errors" in new CellFixture {
    Given("integer cell")
    withCell(_.setCellValue(100.2))
    When("decoder and map result")
    val result = implicits.intCD.map(x => x._1 -> IntegerValue(x._2))(row)
    Then("error occur")
    result shouldBe a[Left[_, _]]
  }
}
