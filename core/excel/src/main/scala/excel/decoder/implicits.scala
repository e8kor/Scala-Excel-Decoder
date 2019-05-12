package excel.decoder

import cats.implicits._
import excel.decoder.cell.{ CellDecoder => CD }
import excel.decoder.row.{ RowDecoder => RD }
import excel.exceptions.ParseError
import excel.ops._
import java.time._
import org.apache.poi.ss.usermodel._
import scala.Predef.doubleWrapper

private[decoder] trait CellImplicits {

  private def doubleToInt(d: Double): Option[Int] =
    if (d.isValidInt) Some(d.toInt) else None

  implicit val stringCD: CD[String] = (cell: Cell) =>
    Either.catchNonFatal(cell.getStringCellValue).leftMap(ParseError(cell, _))

  implicit val intCD: CD[Int] = (cell: Cell) =>
    Either
      .catchNonFatal(cell.getNumericCellValue)
      .map(doubleToInt)
      .leftMap(ParseError(cell, _))
      .flatMap(d => Either.fromOption(d, ParseError(cell, s"$d is not an Int")))

  implicit val doubleCD: CD[Double] = (cell: Cell) =>
    Either.catchNonFatal(cell.getNumericCellValue).leftMap(ParseError(cell, _))

  implicit val localDateTimeCD: CD[LocalDateTime] = (cell: Cell) =>
    Either
      .catchNonFatal(cell.getDateCellValue)
      .map(in => LocalDateTime.ofInstant(in.toInstant, ZoneId.systemDefault))
      .leftMap(ParseError(cell, _))
}

private[decoder] trait ProductImplicits {

  implicit def product1[A1: CD]: RD[A1] = (row: List[Cell]) =>
    for {
      a1 <- row.cell(0).decode[A1]
    } yield a1

  implicit def product2[A1: CD, A2: CD]: RD[(A1, A2)] = (row: List[Cell]) =>
    for {
      a1 <- row.cell(0).decode[A1]
      a2 <- row.cell(1).decode[A2]
    } yield (a1, a2)

  implicit def product3[A1: CD, A2: CD, A3: CD]: RD[(A1, A2, A3)] = (row: List[Cell]) =>
    for {
      a1 <- row.cell(0).decode[A1]
      a2 <- row.cell(1).decode[A2]
      a3 <- row.cell(2).decode[A3]
    } yield (a1, a2, a3)

  implicit def product4[A1: CD, A2: CD, A3: CD, A4: CD]: RD[(A1, A2, A3, A4)] = (row: List[Cell]) =>
    for {
      a1 <- row.cell(0).decode[A1]
      a2 <- row.cell(1).decode[A2]
      a3 <- row.cell(2).decode[A3]
      a4 <- row.cell(3).decode[A4]
    } yield (a1, a2, a3, a4)
}

object implicits extends CellImplicits with ProductImplicits
