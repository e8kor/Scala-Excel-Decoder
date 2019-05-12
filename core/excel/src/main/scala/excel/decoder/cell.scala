package excel.decoder

import org.apache.poi.ss.usermodel._

object cell {

  type CellDecoder[T] = Decoder[Cell, T]

}
