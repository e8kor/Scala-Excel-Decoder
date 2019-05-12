package excel.decoder

import org.apache.poi.ss.usermodel._

object row {

  type RowDecoder[T] = Decoder[List[Cell], T]

}
