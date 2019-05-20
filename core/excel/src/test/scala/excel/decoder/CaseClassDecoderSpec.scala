package excel.decoder

import excel.decoder.implicits._
import excel.ops._
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.scalatest.{ FlatSpec, GivenWhenThen, Matchers }
import scala.collection.mutable

class CaseClassDecoderSpec extends FlatSpec with GivenWhenThen with Matchers {

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

    def row: mutable.ListBuffer[Cell] = rowInstance.clone()

  }

  case class OuterCaseClass(integer: Int, string: InnerCaseClass)

  object OuterCaseClass {

    implicit val dec: RowDecoder[OuterCaseClass] = OuterCaseClass.apply _

  }

  case class InnerCaseClass(integer: Int, string1: String, string2: String)

  object InnerCaseClass {

    implicit val dec: RowDecoder[InnerCaseClass] = InnerCaseClass.apply _

  }

  "Decoder" should "allow to parse complex structures" in new CellFixture {
    Given("row")
    withCell(_.setCellValue(100))
    withCell(_.setCellValue(150))
    withCell(_.setCellValue("Foo"))
    withCell(_.setCellValue("Bar"))
    When("decode row")
    val result = row.decode[OuterCaseClass]
    Then("expected structure created")
    result shouldBe Right(OuterCaseClass(100, InnerCaseClass(150, "Foo", "Bar")))
  }
}
