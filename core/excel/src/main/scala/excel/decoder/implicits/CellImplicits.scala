package excel.decoder.implicits

import cats.implicits._
import excel.decoder.{ CellDecoder => CD }
import excel.exceptions.ParseError
import java.util.Date
import org.apache.poi.ss.usermodel._

/**
 * Set of primitive decoder and lift implicits
 */
private[decoder] trait CellImplicits {

  implicit val stringCD: CD[String] = (cell: Cell) =>
    cell.getCellType match {
      case CellType.STRING  => Right(cell.getStringCellValue)
      case CellType.BOOLEAN => Right(cell.getBooleanCellValue.toString)
      case CellType.NUMERIC => Right(cell.getNumericCellValue.toString)
      case CellType.FORMULA => Right(cell.getCellFormula)
      case other            => Left(ParseError(cell, s"cell type: $other cannot be decoded as string"))
  }

  implicit val intCD: CD[Int] = (cell: Cell) =>
    cell.getCellType match {
      case CellType.NUMERIC =>
        val cellValue = cell.getNumericCellValue
        if (cellValue.isValidInt)
          Right(cellValue.toInt)
        else
          Left(ParseError(cell, s"numeric cell is not integer"))
      case other =>
        Left(ParseError(cell, s"cell type: $other cannot be decoded as integer"))
  }

  implicit val doubleCD: CD[Double] = (cell: Cell) =>
    cell.getCellType match {
      case CellType.NUMERIC => Right(cell.getNumericCellValue)
      case other            => Left(ParseError(cell, s"cell type: $other cannot be decoded as double"))
  }

  implicit val dateTimeCD: CD[Date] = (cell: Cell) =>
    Either.catchNonFatal(cell.getDateCellValue).leftMap(ParseError(cell, _))

  implicit def optionCD[T](implicit dec: CD[T]): CD[Option[T]] = (cell: Cell) =>
    cell.getCellType match {
      case CellType.BLANK => Right(None)
      case _              => dec.decode(cell).map(x => Some(x))
  }

}
