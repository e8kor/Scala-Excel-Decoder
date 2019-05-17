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
  override def rows(book: Workbook): Either[ParseError, List[List[Cell]]] = {
    sheet(book).flatMap { sheet =>
      Either
        .catchNonFatal(sheet.rowIterator().asScala.toList)
        .leftMap(x => ParseError(book, sheet, s"no rows on sheet: ${sheet.getSheetName}", x))
    }.flatMap { rows =>
      rows.map { row =>
        Either
          .catchNonFatal(row.cellIterator().asScala.toList)
          .leftMap(x => ParseError(book, row, s"no cells in row: ${row.getRowNum}", x))
      }.traverse(identity)
    }
  }

  protected def sheet(book: Workbook): Either[ParseError, Sheet]

}

object SheetAddress {

  def apply(name: String): SheetAddress = new SheetAddress {
    override protected def sheet(book: Workbook): Either[ParseError, Sheet] = {
      Either
        .catchNonFatal(book.getSheet(name))
        .leftMap(x => ParseError(book, name, s"missing sheet name: $name", x))
        .flatMap { sheet =>
          if (sheet == null) {
            Left(ParseError(s"missing sheet name: $name"))
          } else {
            Right(sheet)
          }
        }
    }
  }

  def apply(index: Int): SheetAddress = new SheetAddress {
    override protected def sheet(book: Workbook): Either[ParseError, Sheet] = {
      Either
        .catchNonFatal(book.getSheetAt(index))
        .leftMap(x => ParseError(book, index, s"missing sheet index: $index", x))
    }
  }

}

sealed trait AreaAddress extends Address {

  private def getCell(book: Workbook, ref: CellReference): Option[Cell] =
    Either.catchNonFatal(book.getSheet(ref.getSheetName).getRow(ref.getRow).getCell(ref.getCol.toInt)).toOption
//    .leftMap(ex => ParseError(book, ref, s"missing cell: $ref", ex))
//    .flatMap(x => Option(x).toRight(ParseError(s"cell on reference: $ref is null")): Either[ParseError, Cell])

  private def getMatrix(cells: List[Cell]): List[List[Cell]] = for {
    row <- cells.groupBy(_.getRowIndex).toList.sortBy(_._1)
    sorted = row._2.sortBy(_.getColumnIndex)
  } yield sorted

  protected def area(book: Workbook): Either[ParseError, AreaReference]

  override def rows(book: Workbook): Either[ParseError, List[List[Cell]]] = for {
    area <- area(book)
    cells = area.getAllReferencedCells.toList.flatMap(getCell(book, _))
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
      .leftMap(x => ParseError(book, s"missing cells: $start, $end", x))
  }

  private def fromName(name: String)(workbook: Workbook): Either[ParseError, AreaReference] = {
    Either
      .catchNonFatal(new AreaReference(workbook.getName(name).getRefersToFormula, workbook.getSpreadsheetVersion))
      .leftMap(x => ParseError(workbook, s"missing formula: $name", x))
  }

  def apply(
    sheetName: String,
    startColumnIndex: Int,
    startRowIndex: Int,
    endColumnIndex: Int,
    endRowIndex: Int
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
