package excel.decoder.implicits

import excel.decoder.{ RowDecoder => RD }

//TODO: subject for code generation
private[decoder] trait ProductImplicits {

  import scala.language.implicitConversions

  implicit def product1[T, A1](f: A1 => T)(implicit dec: RD[A1]): RD[T] =
    dec.map(a1 => f(a1))

  implicit def product2[T, A1, A2](f: (A1, A2) => T)(implicit dec: RD[(A1, A2)]): RD[T] =
    dec.map {
      case (a1, a2) => f(a1, a2)
    }

  implicit def product3[T, A1, A2, A3](f: (A1, A2, A3) => T)(implicit dec: RD[(A1, A2, A3)]): RD[T] =
    dec.map {
      case (a1, a2, a3) => f(a1, a2, a3)
    }

  implicit def product4[T, A1, A2, A3, A4](f: (A1, A2, A3, A4) => T)(implicit dec: RD[(A1, A2, A3, A4)]): RD[T] =
    dec.map {
      case (a1, a2, a3, a4) => f(a1, a2, a3, a4)
    }

  implicit def product5[T, A1, A2, A3, A4, A5](
    f: (A1, A2, A3, A4, A5) => T
  )(
    implicit dec: RD[(A1, A2, A3, A4, A5)]
  ): RD[T] =
    dec.map {
      case (a1, a2, a3, a4, a5) => f(a1, a2, a3, a4, a5)
    }
}
