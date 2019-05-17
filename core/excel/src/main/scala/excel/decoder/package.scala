package excel

import org.apache.poi.ss.usermodel.Cell

package object decoder {

  /**
   * Type alias for decoding cells
   *
   * @tparam T
   */
  type CellDecoder[T] = Decoder[Cell, T]

  /**
   * Type alias for decoding rows
   *
   * @tparam T
   */
  type RowDecoder[T] = Decoder[List[Cell], T]

}
