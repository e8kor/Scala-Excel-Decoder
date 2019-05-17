package excel

import cats.implicits._
import excel.address.Address
import excel.decoder._
import org.apache.poi.ss.usermodel._

/**
 * Set of ops implicits for apache poi types
 */
package object ops {

  implicit class CellOps(private val it: Cell) extends AnyVal {

    def decode[A](implicit dec: CellDecoder[A]): Decoder.Result[A] = dec(it)

  }

  implicit class RowOps(private val it: List[Cell]) extends AnyVal {

    def cell(index: Int): Cell = it(index)

    def decode[T](implicit dec: RowDecoder[T]): Decoder.Result[T] = dec(it)

  }

  implicit class RowsOps(private val it: List[List[Cell]]) extends AnyVal {

    def decode[T](implicit dec: RowDecoder[T]): Decoder.Result[List[T]] = {
      it.traverse(_.decode)
    }

  }

  implicit class BookOps(private val book: Workbook) extends AnyVal {

    def apply[T: RowDecoder](address: Address): Decoder.Result[List[T]] = {
      address.rows(book).flatMap(_.decode)
    }

  }

}
