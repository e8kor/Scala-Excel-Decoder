package excel.address

import cats.implicits._
import excel.exceptions.ParseError
import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util._

/**
 * Logic Container for reading cells matrix  from workbook
 */
trait Address {

  /**
   * read cells from workbook or return error
   *
   * @param book excel workbook
   * @return error or cells matrix depends on existence of cells and strategy
   */
  def rows(book: Workbook): Either[ParseError, List[List[Cell]]]

}

/**
 * Read all available cells on sheet
 */
sealed trait SheetAddress extends Address {

  import scala.collection.JavaConverters._

  /**
   * read all cells from workbook sheet
   *
   * @param book excel workbook
   * @return error or cells matrix depends on existence of cells and strategy
   */
  override def rows(book: Workbook): Either[ParseError, List[List[Cell]]] =
    sheet(book).map(_.asScala.toList.map(_.asScala.toList))

  /**
   * reads sheet from workbook or return an error in case if it is not exists
   *
   * @param book excel workbook
   * @return sheet or error if its not exists
   */
  protected def sheet(book: Workbook): Either[ParseError, Sheet]

}

object SheetAddress {

  /**
   * read sheet cells with just its name
   *
   * @param name sheet name
   * @return Address instance that will read all cells on sheet
   */
  def apply(name: String): Address = new SheetAddress {
    override protected def sheet(book: Workbook): Either[ParseError, Sheet] = {
      Option(book.getSheet(name)).toRight(ParseError(s"missing sheet name: $name"))
    }
  }

  /**
   * read sheet cells with just its id, indexes starts from 0
   *
   * @param index sheet index
   * @return Address instance that will read all cells on sheet
   */
  def apply(index: Int): Address = new SheetAddress {
    override protected def sheet(book: Workbook): Either[ParseError, Sheet] = {
      Either
        .catchNonFatal(book.getSheetAt(index))
        .leftMap(x => ParseError(book, index, s"missing sheet index: $index", x))
        .flatMap(sheet => Option(sheet).toRight(ParseError(s"missing sheet index: $index")))
    }
  }

}

/**
 * read all cells within sheet area, area can be created with just formula or with coordinates and sheet name
 */
sealed trait AreaAddress extends Address {

  private def getCell(book: Workbook, ref: CellReference): Option[Cell] =
    Either.catchNonFatal(book.getSheet(ref.getSheetName).getRow(ref.getRow).getCell(ref.getCol.toInt)).toOption

  private def getMatrix(cells: List[Cell]): List[List[Cell]] = for {
    row <- cells.groupBy(_.getRowIndex).toList.sortBy(_._1)
    sorted = row._2.sortBy(_.getColumnIndex)
  } yield sorted

  /**
   * create area reference or return error if criteria not met
   *
   * @param book excel book
   * @return error or area reference depends on criteria of selection
   */
  protected def area(book: Workbook): Either[ParseError, AreaReference]

  /**
   * read cells matrix within area reference
   *
   * @param book excel workbook
   * @return error or cells matrix depends on existence of cells and strategy
   */
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
    Right(new AreaReference(start, end, book.getSpreadsheetVersion))
  }

  private def fromName(name: String)(workbook: Workbook): Either[ParseError, AreaReference] = {
    Either
      .catchNonFatal(new AreaReference(workbook.getName(name).getRefersToFormula, workbook.getSpreadsheetVersion))
      .leftMap(x => ParseError(workbook, s"missing formula: $name", x))
  }

  /**
   * read cells that's on sheet and between start and end cell
   *
   * @param sheetName sheet name
   * @param startColumnIndex start column index
   * @param startRowIndex start row index
   * @param endColumnIndex end column index
   * @param endRowIndex end row index
   * @return Address instance to read cell matrix
   */
  def apply(
    sheetName: String,
    startColumnIndex: Int,
    startRowIndex: Int,
    endColumnIndex: Int,
    endRowIndex: Int
  ): Address = {
    val start: CellReference = cell(sheetName, startRowIndex, startColumnIndex)
    val end: CellReference = cell(sheetName, endRowIndex, endColumnIndex)
    new AreaAddress {
      override protected def area(book: Workbook): Either[ParseError, AreaReference] =
        fromCoordinates(start, end)(book)
    }
  }

  /**
   * read cells using formula name
   *
   * @param formula formula name
   * @return Address instance to read cells assigned to formula
   */
  def apply(
    formula: String
  ): Address = new AreaAddress {
    override protected def area(book: Workbook): Either[ParseError, AreaReference] =
      fromName(formula)(book)
  }

}
