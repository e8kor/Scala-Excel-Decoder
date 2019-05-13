val bsettings = Seq(
  scalaVersion := "2.11.12",
  scalacOptions ++= Seq("-deprecation",
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
  addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.3" cross CrossVersion.binary)
)


lazy val root = project.in(file(".")).settings(
  ThisBuild / scalaVersion := "2.11.12",
  ThisBuild / version := "0.1.0-SNAPSHOT",
  ThisBuild / organization := "com.github.e8kor",
  ThisBuild / organizationName := "e8kor"
)
  .aggregate(cli, excel)
  .dependsOn(cli, excel)

lazy val cli = project
  .in(file("cli"))
  .dependsOn(excel)

lazy val excel = project.in(file("core/excel"))
  .settings(bsettings:_*)
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