package excel.book

import cats.implicits._
import excel.exceptions.ParseError
import java.io._
import org.apache.poi.ss.usermodel._
import scala.reflect.io._

/**
 * API for reading Apache POI workbook
 */
object Book {

  import io.tmos.arm.Implicits._

  /**
   * Read workbook or return error if not excel.
   *
   * @param is call-by-name input stream
   * @return workbook or error
   */
  def apply(is: => InputStream): Either[ParseError, Workbook] = for {
    inputStream <- is.manage
    book <- Either
      .catchNonFatal(WorkbookFactory.create(inputStream))
      .leftMap(e => ParseError("error parsing workbook from Input Stream", e))
  } yield book

  /**
   * Read workbook from resources
   *
   * @param resource resource name
   * @return workbook or error
   */
  def apply(resource: String): Either[ParseError, Workbook] = {
    apply(getClass.getClassLoader.getResourceAsStream(resource))
  }

  /**
   * Read workbook by path
   *
   * @param path path to workbook
   * @return workbook or error
   */
  def apply(path: Path): Either[ParseError, Workbook] = {
    if (!path.exists) {
      Left(ParseError(s"no workbook exists at: $path"))
    } else {
      apply(new FileInputStream(path.jfile))
    }
  }

}
