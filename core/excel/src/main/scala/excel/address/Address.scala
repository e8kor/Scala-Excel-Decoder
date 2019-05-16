package excel.address

import cats.implicits._
import excel.exceptions.ParseError
import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util._

trait Address {

  def rows(book: Workbook): Either[ParseError, List[List[Cell]]]

}

object TransposeAddress {

  def apply(address: Address): Address = new Address {
    override def rows(book: Workbook): Either[ParseError, List[List[Cell]]] = address.rows(book).map(_.transpose)
  }

}

sealed trait SheetAddress extends Address {

  import scala.collection.JavaConverters._
  override def rows(book: Workbook): Either[ParseError, List[List[Cell]]] = for {
    x <- sheet(book)
    xs = x.rowIterator().asScala.toList.map(_.cellIterator().asScala.toList)
  } yield xs

  protected def sheet(book: Workbook): Either[ParseError, Sheet]

}

object SheetAddress {

  def apply(name: String): SheetAddress = new SheetAddress {
    override protected def sheet(book: Workbook): Either[ParseError, Sheet] = {
      Either.catchNonFatal(book.getSheet(name)).leftMap(x => ParseError(s"missing sheet name: $name", x))
    }
  }

  def apply(index: Int): SheetAddress = new SheetAddress {
    override protected def sheet(book: Workbook): Either[ParseError, Sheet] = {
      Either.catchNonFatal(book.getSheetAt(index)).leftMap(x => ParseError(s"missing sheet index: $index", x))
    }
  }

}

sealed trait AreaAddress extends Address {

  import scala.Predef.refArrayOps

  private def getCell(book: Workbook, ref: CellReference): Either[ParseError, Cell] = Either
    .catchNonFatal(book.getSheet(ref.getSheetName).getRow(ref.getRow).getCell(ref.getCol.toInt))
    .leftMap(_ => ParseError(s"missing cell: $ref"))

  private def getMatrix(cells: List[Cell]): List[List[Cell]] = for {
    row <- cells.groupBy(_.getRowIndex).toList.sortBy(_._1)
    sorted = row._2.sortBy(_.getColumnIndex)
  } yield sorted

  protected def area(book: Workbook): Either[ParseError, AreaReference]

  override def rows(book: Workbook): Either[ParseError, List[List[Cell]]] = for {
    area <- area(book)
    cells <- area.getAllReferencedCells.toList.traverse(getCell(book, _))
  } yield getMatrix(cells)

}

object AreaAddress {

  private def cell(name: String, rowIndex: Int, columnIndex: Int): CellReference = {
    new CellReference(name, rowIndex, columnIndex, false, false)
  }

  private def fromCoordinates(
    start: CellReference,
    end: CellReference
  )(
    book: Workbook
  ): Either[ParseError, AreaReference] = {
    Either
      .catchNonFatal(new AreaReference(start, end, book.getSpreadsheetVersion))
      .leftMap(x => ParseError(s"missing cells: $start, $end", x))
  }

  private def fromName(name: String)(workbook: Workbook): Either[ParseError, AreaReference] = {
    Either
      .catchNonFatal(new AreaReference(workbook.getName(name).getRefersToFormula, workbook.getSpreadsheetVersion))
      .leftMap(x => ParseError(s"missing formula: $name", x))
  }

  def apply(
    sheetName: String,
    startRowIndex: Int,
    startColumnIndex: Int,
    endRowIndex: Int,
    endColumnIndex: Int
  ): AreaAddress = {
    val start: CellReference = cell(sheetName, startRowIndex, startColumnIndex)
    val end: CellReference = cell(sheetName, endRowIndex, endColumnIndex)
    new AreaAddress {
      override protected def area(book: Workbook): Either[ParseError, AreaReference] =
        fromCoordinates(start, end)(book)
    }
  }

  def apply(
    name: String
  ): AreaAddress = new AreaAddress {
    override protected def area(book: Workbook): Either[ParseError, AreaReference] =
      fromName(name)(book)
  }

}
