package excel.exceptions

import org.apache.poi.ss.usermodel.Cell

sealed trait ParseError

case class MParseError(text: String) extends ParseError

object ParseError {

  def apply(text: String): ParseError = MParseError(text)

  def apply(text: String, e: Throwable): ParseError = MParseError(e.getMessage)

  def apply(cell: Cell, e: Throwable): ParseError = MParseError(e.getMessage)

  def apply(cell: Cell, text: String): ParseError = MParseError(text)

}
