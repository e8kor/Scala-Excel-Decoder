val bsettings = Seq(
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
    "-Yno-predef",
    "-Ywarn-unused-import",
    "-Ypartial-unification"
  ),
  crossScalaVersions := Seq("2.11.12", "2.12.8"),
  publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath + "/.m2/repository"))),
  addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.3" cross CrossVersion.binary)
)

import scala.xml.{ Elem, Node => XmlNode, NodeSeq => XmlNodeSeq }
import scala.xml.transform.{ RewriteRule, RuleTransformer }

lazy val publishSettings = Seq(
  releaseCrossBuild := true,
  homepage := Some(url("https://github.com/e8kor/Scala-Excel-Decoder")),
  licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  publishTo := {
//    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some(Resolver.file("file", new File(Path.userHome.absolutePath + "/.m2/repository")))
//      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some(Resolver.file("file", new File(Path.userHome.absolutePath + "/.m2/repository")))
//      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  autoAPIMappings := true,
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/e8kor/Scala-Excel-Decoder"),
      "scm:git:git@github.com:e8kor/Scala-Excel-Decoder.git"
    )
  ),
  developers := List(
    Developer("e8kor", "Eugene Korniichuk", "eugene.korniichuk@gmail.com", url("https://medium.com/@e8kor"))
  ),
  pomPostProcess := { (node: XmlNode) =>
    new RuleTransformer(
      new RewriteRule {
        private def isTestScope(elem: Elem): Boolean =
          elem.label == "dependency" && elem.child.exists(child => child.label == "scope" && child.text == "test")

        override def transform(node: XmlNode): XmlNodeSeq = node match {
          case elem: Elem if isTestScope(elem) => Nil
          case _                               => node
        }
      }
    ).transform(node).head
  }
)

lazy val root = project
  .in(file("."))
  .settings(
    ThisBuild / scalaVersion := "2.11.12",
    ThisBuild / version := "0.1.0-SNAPSHOT",
    ThisBuild / organization := "com.github.e8kor",
    ThisBuild / organizationName := "e8kor"
  )
  .settings(bsettings: _*)
  .settings(publishSettings:_*)
  .aggregate(excel)
  .dependsOn(excel)

lazy val excel = project
  .in(file("core/excel"))
  .settings(bsettings: _*)
  .settings(publishSettings:_*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "org.typelevel" %% "cats-testkit" % "1.6.0"
    ),
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.3",
      "io.tmos" %% "arm4s" % "1.1.0",
      "org.typelevel" %% "cats-core" % "1.6.0",
      "org.apache.poi" % "poi-ooxml" % "4.1.0"
    )
  )
