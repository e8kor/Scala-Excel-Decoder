import sbt._
import sbt.Keys._
import scalafix.sbt.ScalafixPlugin, ScalafixPlugin.autoImport._
import scoverage.ScoverageKeys._

object compile extends AutoPlugin {

  override def trigger = allRequirements

  override def buildSettings: Seq[Def.Setting[_]] = Seq(
    ThisBuild / scalaVersion := "2.11.12",
    ThisBuild / crossScalaVersions := Seq("2.11.12", "2.12.8"),
    ThisBuild / organization := "com.github.e8kor",
    ThisBuild / organizationName := "e8kor",
    coverageHighlighting := true,
    coverageScalacPluginVersion := "1.3.1",
    coverageExcludedPackages := "excel.decoder.implicits.TupleImplicits;excel.decoder.implicits.ProductImplicits"
  )

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalacOptions ++= Seq(
      "-language:_",
      "-unchecked",
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-feature",
      "-Yrangepos",
      "-Ypartial-unification",
      "-Ywarn-unused",
      "-Xexperimental"
    ),
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF"),
    addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.3" cross CrossVersion.binary),
    addCompilerPlugin(scalafixSemanticdb)
  )

}
