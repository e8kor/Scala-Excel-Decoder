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

private[decoder] trait TupleImplicits {

  implicit def tuple1[A1: CD]: RD[A1] = (row: List[Cell]) =>
    for {
      a1 <- row.cell(0).decode[A1]
    } yield a1

  implicit def tuple2[A1: CD, A2: CD]: RD[(A1, A2)] = (row: List[Cell]) =>
    for {
      a1 <- row.cell(0).decode[A1]
      a2 <- row.cell(1).decode[A2]
    } yield (a1, a2)

  implicit def tuple3[A1: CD, A2: CD, A3: CD]: RD[(A1, A2, A3)] = (row: List[Cell]) =>
    for {
      a1 <- row.cell(0).decode[A1]
      a2 <- row.cell(1).decode[A2]
      a3 <- row.cell(2).decode[A3]
    } yield (a1, a2, a3)

  implicit def tuple4[A1: CD, A2: CD, A3: CD, A4: CD]: RD[(A1, A2, A3, A4)] = (row: List[Cell]) =>
    for {
      a1 <- row.cell(0).decode[A1]
      a2 <- row.cell(1).decode[A2]
      a3 <- row.cell(2).decode[A3]
      a4 <- row.cell(3).decode[A4]
    } yield (a1, a2, a3, a4)

  implicit def tuple5[A1: CD, A2: CD, A3: CD, A4: CD, A5: CD]: RD[(A1, A2, A3, A4, A5)] = (row: List[Cell]) =>
    for {
      a1 <- row.cell(0).decode[A1]
      a2 <- row.cell(1).decode[A2]
      a3 <- row.cell(2).decode[A3]
      a4 <- row.cell(3).decode[A4]
      a5 <- row.cell(4).decode[A5]
    } yield (a1, a2, a3, a4, a5)
}

private[decoder] trait ProductImplicits {

  import scala.language.implicitConversions

  implicit def product1[T, A1](f: A1 => T)(implicit dec: RD[A1]): RD[T] =
    dec.map(a1 => f(a1))

  implicit def product2[T, A1, A2](f: (A1, A2) => T)(implicit dec: RD[(A1, A2)]): RD[T] =
    dec.map {
      case (a1, a2) => f(a1, a2)
    }

  implicit def product3[T, A1, A2, A3](f: (A1, A2, A3) => T)(implicit dec: RD[(A1, A2, A3)]): RD[T] =
    dec.map {
      case (a1, a2, a3) => f(a1, a2, a3)
    }

  implicit def product4[T, A1, A2, A3, A4](f: (A1, A2, A3, A4) => T)(implicit dec: RD[(A1, A2, A3, A4)]): RD[T] =
    dec.map {
      case (a1, a2, a3, a4) => f(a1, a2, a3, a4)
    }

  implicit def product5[T, A1, A2, A3, A4, A5](
    f: (A1, A2, A3, A4, A5) => T
  )(
    implicit dec: RD[(A1, A2, A3, A4, A5)]
  ): RD[T] =
    dec.map {
      case (a1, a2, a3, a4, a5) => f(a1, a2, a3, a4, a5)
    }
}

object implicits extends ProductImplicits with TupleImplicits with CellImplicits
