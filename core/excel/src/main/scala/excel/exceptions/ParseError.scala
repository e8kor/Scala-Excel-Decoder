package excel.exceptions

import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util._

sealed trait ParseError

private[exceptions] case class MParseError(text: String) extends ParseError

object ParseError {

  def apply(book: Workbook, sheetName: String, text: String, e: Throwable): ParseError = MParseError(text)

  def apply(book: Workbook, index: Int, text: String, e: Throwable): ParseError = MParseError(text)

  def apply(book: Workbook, ref: CellReference, text: String, e: Throwable): ParseError =
    MParseError(text + ": " + e.toString)

  def apply(book: Workbook, text: String, e: Throwable): ParseError = MParseError(text)

  def apply(book: Workbook, sheet: Sheet, text: String, e: Throwable): ParseError = MParseError(text)

  def apply(book: Workbook, row: Row, text: String, e: Throwable): ParseError = MParseError(text)

  def apply(text: String): ParseError = MParseError(text)

  def apply(text: String, e: Throwable): ParseError = MParseError(text + ": " + e.getMessage)

  def apply(cell: Cell, e: Throwable): ParseError = MParseError(cell.getAddress.formatAsString() + ": " + e.getMessage)

  def apply(cell: Cell, text: String): ParseError = MParseError(text)

}
