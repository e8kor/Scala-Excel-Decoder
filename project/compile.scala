import sbt.{ Def, _ }
import sbt.Keys._

object compile {

  val boilerplate: Def.Initialize[Task[Seq[sbt.File]]] = (sourceManaged in Compile).map(Boilerplate.gen)

  val settings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := "2.11.12",
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-unchecked",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Xfuture",
      "-Ywarn-unused-import",
      "-Ypartial-unification"
    ),
    crossScalaVersions := Seq("2.11.12", "2.12.8"),
    addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.3" cross CrossVersion.binary)
  )

}
