package excel.decoder

import excel.exceptions.ParseError

trait Decoder[X, Y] extends (X => Decoder.Result[Y]) with Serializable { self =>

  def decode(x: X): Decoder.Result[Y] = apply(x)

  def map[Z](f: Y => Z): Decoder[X, Z] = (x: X) =>
    self(x) match {
      case Right(y) => Right(f(y))
      case Left(e)  => Left(e)
  }

  def flatMap[Z](f: Y => Decoder[Y, Z]): Decoder[X, Z] = { x: X =>
    self(x) match {
      case Right(y) => f(y)(y)
      case Left(e)  => Left(e)
    }
  }

}

object Decoder {

  type Result[T] = Either[ParseError, T]

}
