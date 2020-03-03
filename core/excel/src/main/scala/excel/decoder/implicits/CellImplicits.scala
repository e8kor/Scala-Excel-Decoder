package excel.decoder.implicits

import cats.implicits._
import excel.decoder.{ RowDecoder => RD }
import excel.exceptions.ParseError
import java.util.Date
import org.apache.poi.ss.usermodel._
import scala.collection.mutable

/**
 * Set of primitive decoder and lift implicits
 */
private[decoder] trait CellImplicits {

  private def withCell[T](f: Cell => Either[ParseError, T]): RD[T] = (cells: mutable.ListBuffer[Cell]) => {
    val cell = cells.head
    cells -= cell
    f(cell)
  }

  /**
   * Decoder for reading potentially blank cells
   */
  implicit def optionDecoder[T](implicit dec: RD[T]): RD[Option[T]] = (cells: mutable.ListBuffer[Cell]) => {
    import cells.head
    if (CellType.BLANK == head.getCellType) {
      cells -= head
      Right(None)
    } else {
      dec.decode(cells).map(x => Some(x))
    }
  }

  /**
   * Decoder for reading text cells
   */
  implicit val stringDecoder: RD[String] = withCell { cell =>
    cell.getCellType match {
      case CellType.STRING  => Right(cell.getStringCellValue)
      case CellType.BOOLEAN => Right(cell.getBooleanCellValue.toString)
      case CellType.NUMERIC => Right(cell.getNumericCellValue.toString)
      case CellType.FORMULA => Right(cell.getCellFormula)
      case other            => Left(ParseError(cell, s"cell type: $other cannot be decoded as string"))
    }
  }

  /**
   * Decoder for reading natural number cells
   */
  implicit val integerDecoder: RD[Int] = withCell { cell =>
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
  }

  /**
   * Decoder for reading all numeric cells
   */
  implicit val doubleDecoder: RD[Double] = withCell { cell =>
    cell.getCellType match {
      case CellType.NUMERIC => Right(cell.getNumericCellValue)
      case other            => Left(ParseError(cell, s"cell type: $other cannot be decoded as double"))
    }
  }

  /**
   * Decoder for reading date cells
   */
  implicit val dateTimeDecoder: RD[Date] = withCell { cell =>
    cell.getCellType match {
      case CellType.NUMERIC => Either.catchNonFatal(cell.getDateCellValue).leftMap(ParseError(cell, _))
      case other            => Left(ParseError(cell, s"cell type: $other cannot be decoded as date"))
    }
  }

}
