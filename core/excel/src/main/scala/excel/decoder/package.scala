package excel

import org.apache.poi.ss.usermodel.Cell
import scala.collection.mutable

package object decoder {

  /**
   * Type alias for decoding rows
   *
   * @tparam T
   */
  type RowDecoder[T] = Decoder[List[Cell], (List[Cell], T)]

}
