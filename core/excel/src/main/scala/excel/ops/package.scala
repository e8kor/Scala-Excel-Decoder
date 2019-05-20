package excel

import cats.implicits._
import excel.address.Address
import excel.decoder._
import org.apache.poi.ss.usermodel._
import scala.collection.mutable

/**
 * Set of ops implicits for apache poi types
 */
package object ops {

//  implicit class CellOps(private val it: Cell) extends AnyVal {
//
//    /**
//     * Parse cell value using decoder
//     *
//     * @param dec decoder instance
//     * @tparam T output type
//     * @return error or output instance
//     */
//    def decode[T](implicit dec: CellDecoder[T]): Decoder.Result[T] = dec(it)
//
//  }

  implicit class RowOps(private val it: mutable.ListBuffer[Cell]) extends AnyVal {

    /**
     * Parse row using decoder
     *
     * @param dec decoder instance
     * @tparam T output type
     * @return error or output instance
     */
    def decode[T](implicit dec: RowDecoder[T]): Decoder.Result[T] = dec(it)

  }

  implicit class RowsOps(private val it: List[mutable.ListBuffer[Cell]]) extends AnyVal {

    /**
     * Parse rows using decoder
     *
     * @param dec decoder instance
     * @tparam T output type
     * @return error or list
     */
    def decode[T](implicit dec: RowDecoder[T]): Decoder.Result[List[T]] = {
      it.traverse(_.decode)
    }

  }

  implicit class BookOps(private val book: Workbook) extends AnyVal {

    /**
     * Parse book using address to read matrix and decoder to parse rows
     *
     * @param address rows reader
     * @param dec decoder instance
     * @tparam T output type
     * @return error or list
     */
    def apply[T](address: Address)(implicit dec: RowDecoder[T]): Decoder.Result[List[T]] = {
      address.rows(book).flatMap(_.decode)
    }

  }

}
