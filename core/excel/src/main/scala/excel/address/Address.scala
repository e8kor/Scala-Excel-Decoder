package excel.address

import cats.implicits._
import excel.exceptions.ParseError
import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Logic Container for reading cells matrix  from workbook
 */
sealed trait Address {

  /**
   * read cells from workbook or return error
   *
   * @param book excel workbook
   * @return error or cells matrix depends on existence of cells and strategy
   */
  def rows(book: Workbook): Either[ParseError, List[mutable.ListBuffer[Cell]]]

}

sealed trait Builder[T <: Builder[T]] { self =>
  def exceptRows(indexes: Int*): T
  def exceptColumns(indexes: Int*): T
  def build(): Address
}

object AddressBuilder {
  private def ref(coords: (Int, Int)) = new CellReference(coords._1, coords._2)
  def area(name: String)(from: (Int, Int), to: (Int, Int)): AreaBuilder =
    AreaBuilder(ref(from), ref(to), List.empty, List.empty, Left(name))
  def area(index: Int)(from: (Int, Int), to: (Int, Int)): AreaBuilder =
    AreaBuilder(ref(from), ref(to), List.empty, List.empty, Right(index))
  def formula(formula: String): FormulaBuilder = FormulaBuilder(List.empty, List.empty, formula)
  def sheet(name: String): SheetBuilder = SheetBuilder(List.empty, List.empty, Left(name))
  def sheet(index: Int): SheetBuilder = SheetBuilder(List.empty, List.empty, Right(index))
}

case class SheetBuilder private (
  excludedRowIndexes: List[Int],
  excludedColumnIndexes: List[Int],
  from: Either[String, Int]
) extends Builder[SheetBuilder] {
  def exceptRows(indexes: Int*): SheetBuilder = copy(excludedRowIndexes = indexes.toList)
  def exceptColumns(indexes: Int*): SheetBuilder = copy(excludedColumnIndexes = indexes.toList)
  def build(): Address = new Address {

    private def filterPredicate(cell: Cell) = {
      excludedColumnIndexes.contains(cell.getColumnIndex) || excludedRowIndexes.contains(cell.getRowIndex)
    }

    override def rows(book: Workbook): Either[ParseError, List[mutable.ListBuffer[Cell]]] = {
      import scala.collection.JavaConverters._
      Either
        .catchNonFatal(from.fold(book.getSheet, book.getSheetAt))
        .leftMap(x => ParseError(book, s"missing sheet at: $from", x))
        .flatMap(x => Option(x).toRight(ParseError(s"missing sheet at: $from")))
        .map { rows =>
          for {
            row <- rows.rowIterator().asScala.toList
            cells = row.cellIterator().asScala.filterNot(filterPredicate).to[mutable.ListBuffer]
          } yield cells
        }
    }
  }
}

case class AreaBuilder private (
  start: CellReference,
  end: CellReference,
  excludedRowIndexes: List[Int],
  excludedColumnIndexes: List[Int],
  from: Either[String, Int]
) extends Builder[AreaBuilder] {

  def exceptRows(indexes: Int*): AreaBuilder = copy(excludedRowIndexes = indexes.toList)
  def exceptColumns(indexes: Int*): AreaBuilder = copy(excludedColumnIndexes = indexes.toList)

  def build(): Address = new Address {

    private def filterPredicate(cell: Cell) = {
      excludedColumnIndexes.contains(cell.getColumnIndex) || excludedRowIndexes.contains(cell.getRowIndex)
    }

    override def rows(book: Workbook): Either[ParseError, List[mutable.ListBuffer[Cell]]] = {
      import scala.collection.JavaConverters._
      Either
        .catchNonFatal(from.fold(book.getSheet, book.getSheetAt))
        .leftMap(x => ParseError(book, s"missing sheet at: $from", x))
        .flatMap(x => Option(x).toRight(ParseError(s"missing sheet at: $from")))
        .map { rows =>
          for {
            row <- rows.rowIterator().asScala.toList
            cells = row.cellIterator().asScala.filterNot(filterPredicate).to[mutable.ListBuffer]
          } yield cells
        }
    }
  }
}

case class FormulaBuilder private (
  excludedRowIndexes: List[Int],
  excludedColumnIndexes: List[Int],
  formula: String
) extends Builder[FormulaBuilder] {

  def exceptRows(indexes: Int*): FormulaBuilder = copy(excludedRowIndexes = indexes.toList)
  def exceptColumns(indexes: Int*): FormulaBuilder = copy(excludedColumnIndexes = indexes.toList)
  def build(): Address = new Address {

    private def filterPredicate(cell: Cell) = {
      excludedColumnIndexes.contains(cell.getColumnIndex) || excludedRowIndexes.contains(cell.getRowIndex)
    }

    private def fromName(name: String)(workbook: Workbook): Either[ParseError, AreaReference] = {
      Either
        .catchNonFatal(new AreaReference(workbook.getName(name).getRefersToFormula, workbook.getSpreadsheetVersion))
        .leftMap(x => ParseError(workbook, s"missing formula: $name", x))
        .flatMap(x => Option(x).toRight(ParseError(s"missing formula: $name")))
    }

    private def getCell(book: Workbook, ref: CellReference): Option[Cell] =
      Either.catchNonFatal(book.getSheet(ref.getSheetName).getRow(ref.getRow).getCell(ref.getCol.toInt)).toOption

    private def getMatrix(cells: List[Cell]): List[mutable.ListBuffer[Cell]] = for {
      row <- cells.groupBy(_.getRowIndex).toList.sortBy(_._1)
      sorted = row._2.filterNot(filterPredicate).sortBy(_.getColumnIndex).to[mutable.ListBuffer]
    } yield sorted

    override def rows(book: Workbook): Either[ParseError, List[mutable.ListBuffer[Cell]]] = for {
      area <- fromName(formula)(book)
      cells = area.getAllReferencedCells.toList.flatMap(getCell(book, _))
    } yield getMatrix(cells)

  }
}
