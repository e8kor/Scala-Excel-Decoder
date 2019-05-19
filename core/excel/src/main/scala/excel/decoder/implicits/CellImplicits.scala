package excel.decoder.implicits

import cats.implicits._
import excel.decoder.{ RowDecoder => RD }
import excel.exceptions.ParseError
import java.util.Date
import org.apache.poi.ss.usermodel._

/**
 * Set of primitive decoder and lift implicits
 */
private[decoder] trait CellImplicits {

  /**
   * Decoder for reading text cells
   */
  implicit val stringCD: RD[String] = (cells: List[Cell]) => {
    val cell = cells.head
    cell.getCellType match {
      case CellType.STRING  => Right(cells.tail, cell.getStringCellValue)
      case CellType.BOOLEAN => Right(cells.tail, cell.getBooleanCellValue.toString)
      case CellType.NUMERIC => Right(cells.tail, cell.getNumericCellValue.toString)
      case CellType.FORMULA => Right(cells.tail, cell.getCellFormula)
      case other            => Left(ParseError(cell, s"cell type: $other cannot be decoded as string"))
    }
  }

  /**
   * Decoder for reading natural number cells
   */
  implicit val intCD: RD[Int] = (cells: List[Cell]) => {
    val cell = cells.head
    cell.getCellType match {
      case CellType.NUMERIC =>
        val cellValue = cell.getNumericCellValue
        if (cellValue.isValidInt)
          Right(cells.tail, cellValue.toInt)
        else
          Left(ParseError(cell, s"numeric cell is not integer"))
      case other =>
        Left(ParseError(cell, s"cell type: $other cannot be decoded as integer"))
    }
  }

  /**
   * Decoder for reading all numeric cells
   */
  implicit val doubleCD: RD[Double] = (cells: List[Cell]) => {
    val cell = cells.head
    cell.getCellType match {
      case CellType.NUMERIC => Right(cells.tail, cell.getNumericCellValue)
      case other            => Left(ParseError(cell, s"cell type: $other cannot be decoded as double"))
    }
  }

  /**
   * Decoder for reading date cells
   */
  implicit val dateTimeCD: RD[Date] = (cells: List[Cell]) => {
    val cell = cells.head
    cell.getCellType match {
      case CellType.NUMERIC => Either.catchNonFatal((cells.tail, cell.getDateCellValue)).leftMap(ParseError(cell, _))
      case other            => Left(ParseError(cell, s"cell type: $other cannot be decoded as date"))
    }
  }

  /**
   * Decoder for reading potentially blank cells
   */
  implicit def optionRD[T](implicit dec: RD[T]): RD[Option[T]] = (cells: List[Cell]) => {
    val cell = cells.head
    cell.getCellType match {
      case CellType.BLANK => Right(cells.tail -> None)
      case _              => dec.decode(cells).map(x => (x._1, Some(x._2)))
    }
  }
}
