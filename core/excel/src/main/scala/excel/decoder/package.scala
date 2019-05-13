package excel

import org.apache.poi.ss.usermodel.Cell

package object decoder {

  type CellDecoder[T] = Decoder[Cell, T]

  type RowDecoder[T] = Decoder[List[Cell], T]

}
