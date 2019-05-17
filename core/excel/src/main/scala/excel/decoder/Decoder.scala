package excel.decoder

import excel.exceptions.ParseError

/**
 * Definition of Decoder, contains logic to read Y from X source
 *
 * @tparam X input type parameter
 * @tparam Y output type parameter
 */
trait Decoder[X, Y] extends (X => Decoder.Result[Y]) with Serializable {
  self =>

  /**
   * wrapper for apply method with meaningful name
   *
   * @param x input source
   * @return return error or output parameter
   */
  def decode(x: X): Decoder.Result[Y] = apply(x)

  /**
   * transform result to desirable type
   *
   * @param f transformation
   * @tparam Z desirable type parameter
   * @return new decoder from type X to Z
   */
  def map[Z](f: Y => Z): Decoder[X, Z] = (x: X) =>
    self(x) match {
      case Right(y) => Right(f(y))
      case Left(e)  => Left(e)
  }

  /**
   * transformation from output type to new decoder
   *
   * @param f transformation
   * @tparam Z output type parameter
   * @return new decoder from type X to Z
   */
  def flatMap[Z](f: Y => Decoder[Y, Z]): Decoder[X, Z] = { x: X =>
    self(x) match {
      case Right(y) => f(y)(y)
      case Left(e)  => Left(e)
    }
  }

}

object Decoder {

  /**
   * alias for decoder results
   *
   * @tparam T
   */
  type Result[T] = Either[ParseError, T]

}
