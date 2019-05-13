package excel.decoder.implicits

import cats.implicits._
import excel.decoder.{ CellDecoder => CD, RowDecoder => RD }
import excel.ops._
import org.apache.poi.ss.usermodel.Cell

//TODO: subject for code generation
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
