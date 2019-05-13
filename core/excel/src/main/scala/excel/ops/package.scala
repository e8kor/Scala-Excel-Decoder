package excel

import cats.implicits._
import excel.address.Address
import excel.decoder.Decoder
import excel.decoder.cell.CellDecoder
import excel.decoder.row.RowDecoder
import org.apache.poi.ss.usermodel._

package object ops {

  implicit class CellOps(val it: Cell) extends AnyVal {

    def decode[A](implicit dec: CellDecoder[A]): Decoder.Result[A] = dec(it)

  }

  implicit class RowOps(val it: List[Cell]) extends AnyVal {

    def cell(index: Int): Cell = it(index)

    def decode[T](implicit dec: RowDecoder[T]): Decoder.Result[T] = dec(it)

  }

  implicit class RowsOps(val it: List[List[Cell]]) extends AnyVal {
    import scala.language.postfixOps

    def decode[T](implicit dec: RowDecoder[T]): Decoder.Result[List[T]] = {
      it traverse (_ decode)
    }

  }

  implicit class BookOps(val book: Workbook) extends AnyVal {
    import scala.language.postfixOps

    def apply[T: RowDecoder](address: Address): Decoder.Result[List[T]] = {
      (address rows book) >>= (_ decode)
    }

  }

}
