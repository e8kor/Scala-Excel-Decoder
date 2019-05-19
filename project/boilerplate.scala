import sbt._
import sbt.Keys.sourceManaged

/**
 * Generate a range of boilerplate classes that would be tedious to write and maintain by hand.
 *
 * Copied, with some modifications, from
 * [[https://github.com/milessabin/shapeless/blob/master/project/Boilerplate.scala Shapeless]].
 *
 * @author Miles Sabin
 * @author Kevin Wright
 */
object boilerplate {

  import scala.StringContext._

  val templates: Seq[Template] = Seq(GenTupleImplicits, GenProductImplicits)

  val header = "// auto-generated boilerplate"
  val maxArity = 22

  /**
   * Return a sequence of the generated files.
   *
   * As a side-effect, it actually generates them...
   */
  def gen(dir: File): Seq[File] = templates.map { template =>
    val tgtFile = template.filename(dir)
    IO.write(tgtFile, template.body)
    tgtFile
  }

  val generate: Def.Initialize[Task[Seq[sbt.File]]] = (sourceManaged in Compile).map(boilerplate.gen)

  implicit class BlockHelper(val sc: StringContext) extends AnyVal {
    def block(args: Any*): String = {
      val interpolated = sc.standardInterpolator(treatEscapes, args)
      val rawLines = interpolated.split('\n')
      val trimmedLines = rawLines.map(_.dropWhile(_.isWhitespace))
      trimmedLines.mkString("\n")
    }
  }

  class TemplateVals(val arity: Int) {
    val synTypes: Seq[String] = (0 until arity).map(n => s"A$n")
    val synVals: Seq[String] = (0 until arity).map(n => s"a$n")

    val `A..N`: String = synTypes.mkString(", ")
    val `a..n`: String = synVals.mkString(", ")
    val `_.._` : String = Seq.fill(arity)("_").mkString(", ")
    val `(A..N)` : String = if (arity == 1) "Tuple1[A0]" else synTypes.mkString("(", ", ", ")")
    val `(_.._)` : String = if (arity == 1) "Tuple1[_]" else Seq.fill(arity)("_").mkString("(", ", ", ")")
    val `(a..n)` : String = if (arity == 1) "Tuple1(a0)" else synVals.mkString("(", ", ", ")")
  }

  /**
   * Blocks in the templates below use a custom interpolator, combined with post-processing to
   * produce the body.
   *
   * - The contents of the `header` val is output first
   * - Then the first block of lines beginning with '|'
   * - Then the block of lines beginning with '-' is replicated once for each arity,
   * with the `templateVals` already pre-populated with relevant relevant vals for that arity
   * - Then the last block of lines prefixed with '|'
   *
   * The block otherwise behaves as a standard interpolated string with regards to variable
   * substitution.
   */
  trait Template {
    def filename(root: File): File

    def content(tv: TemplateVals): String

    def range: IndexedSeq[Int] = 1 to maxArity

    def body: String = {
      val headerLines = header.split('\n')
      val raw = range.map(n => content(new TemplateVals(n)).split('\n').filterNot(_.isEmpty))
      val preBody = raw.head.takeWhile(_.startsWith("|")).map(_.tail)
      val instances = raw.flatMap(_.filter(_.startsWith("-")).map(_.tail))
      val postBody = raw.head.dropWhile(_.startsWith("|")).dropWhile(_.startsWith("-")).map(_.tail)
      (headerLines ++ preBody ++ instances ++ postBody).mkString("\n")
    }
  }
//
//  object GenTupleImplicits extends Template {
//
//    override def range: IndexedSeq[Int] = 1 to maxArity
//
//    override def filename(root: File): File = root / "excel" / "decoder" / "implicits" / "TupleImplicits.scala"
//
//    override def content(tv: TemplateVals): String = {
//      import tv._
//
//      val lines: Seq[String] = (0 until arity).map(n => s"(row, a$n) <- row.decode[A$n]")
//
//      val instances = synTypes.map(tpe => s"decode$tpe: RD[$tpe]").mkString(", ")
//
//      // @formatter:off
//      block"""
//        |package excel.decoder.implicits
//        |
//        |import cats.implicits._
//        |import excel.decoder.{ RowDecoder => RD }
//        |import excel.ops._
//        |import org.apache.poi.ss.usermodel.Cell
//        |
//        |private [implicits] trait TupleImplicits {
//        -
//        -  /**
//        -   * @group Tuple
//        -   */
//        -  implicit final def tuple$arity[${`A..N`}](implicit $instances): RD[${`(A..N)`}] = (row: List[Cell]) =>
//        -    for {
//        -      ${lines.mkString("\n        -      ")}
//        -    } yield (row, ${`(a..n)`})
//        -
//        |}
//      """
//      // @formatter:on
//    }
//  }
//
  object GenTupleImplicits extends Template {

    override def range: IndexedSeq[Int] = 1 to maxArity

    override def filename(root: File): File = root / "excel" / "decoder" / "implicits" / "TupleImplicits.scala"

    override def content(tv: TemplateVals): String = {
      import tv._

      val lines: Seq[String] = {
        (0 until arity).map(n => s"(row, a$n) <- row.decode[A$n]")
      }

      val instances = synTypes.map(tpe => s"decode$tpe: RD[$tpe]").mkString(", ")

      // @formatter:off
      block"""
        |package excel.decoder.implicits
        |
        |import cats.implicits._
        |import excel.decoder.{ RowDecoder => RD }
        |import excel.ops._
        |import org.apache.poi.ss.usermodel.Cell
        |
        |private [implicits] trait TupleImplicits {
        -
        -  /**
        -   * @group Tuple
        -   */
        -  implicit final def tuple$arity[${`A..N`}](implicit $instances): RD[${`(A..N)`}] = (row: List[Cell]) =>
        -    for {
        -      ${lines.mkString("\n        -      ")}
        -    } yield (row, ${`(a..n)`})
        -
        |}
      """
      // @formatter:on
    }
  }

  object GenProductImplicits extends Template {

    override def range: IndexedSeq[Int] = 1 to maxArity

    override def filename(root: File): File = root / "excel" / "decoder" / "implicits" / "ProductImplicits.scala"

    override def content(tv: TemplateVals): String = {
      import tv._

      // @formatter:off
      block"""
        |package excel.decoder.implicits
        |
        |import excel.decoder.{ RowDecoder => RD}
        |import scala.language.implicitConversions
        |
        |private [implicits] trait ProductImplicits {
        -
        -  /**
        -   * @group Product
        -   */
        -  implicit final def product$arity[T, ${`A..N`}](f: (${`A..N`}) => T)(implicit dec: RD[${`(A..N)`}]): RD[T] =
        -  dec.map {
        -    case (row, ${`(a..n)`}) => (row, f(${`a..n`}))
        -  }
        -
        |}
      """
      // @formatter:on
    }
  }

}
