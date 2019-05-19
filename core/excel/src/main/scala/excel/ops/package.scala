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

  implicit class RowOps(private val it: List[Cell]) extends AnyVal {

    /**
     * Parse row using decoder
     *
     * @param dec decoder instance
     * @tparam T output type
     * @return error or output instance
     */
    def decode[T](implicit dec: RowDecoder[T]): Decoder.Result[(List[Cell], T)] = dec(it)

  }

  implicit class RowsOps(private val it: List[List[Cell]]) extends AnyVal {

    /**
     * Parse rows using decoder
     *
     * @param dec decoder instance
     * @tparam T output type
     * @return error or list
     */
    def decode[T](implicit dec: RowDecoder[T]): Decoder.Result[List[T]] = {
      it.traverse(_.decode.map(_._2))
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
