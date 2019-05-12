package excel.decoder

import excel.exceptions.ParseError

trait Decoder[X, Y] extends (X => Decoder.Result[Y]) {

  def map[Z](f: Y => Z): Decoder[X, Z] = {
    (apply _).andThen {
      case Right(y) => Right(f(y))
      case Left(e)  => Left(e)
    }
  }

  def flatMap[Z](f: Y => Decoder.Result[Z]): Decoder[X, Z] = {
    (apply _).andThen {
      case Right(y) => f(y)
      case Left(e)  => Left(e)
    }
  }

}

object Decoder {

  import scala.language.implicitConversions

  type Result[T] = Either[ParseError, T]

  implicit private[decoder] def apply[X, Y](f: X => Result[Y]): Decoder[X, Y] = new Decoder[X, Y] {
    override def apply(x: X): Result[Y] = f(x)
  }

}
