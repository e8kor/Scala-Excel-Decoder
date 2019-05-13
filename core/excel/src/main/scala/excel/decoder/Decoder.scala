package excel.decoder

import excel.decoder.Decoder.Result
import excel.exceptions.ParseError

trait Decoder[X, Y] extends (X => Decoder.Result[Y]) with Serializable { self =>

  def decode(x: X): Decoder.Result[Y] = apply(x)

  def map[Z](f: Y => Z): Decoder[X, Z] = {
    flatMap(f.andThen(x => Right(x)))
  }

  def flatMap[Z](f: Y => Decoder.Result[Z]): Decoder[X, Z] = new Decoder[X, Z] {

    override def apply(v1: X): Result[Z] = self(v1) match {
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
