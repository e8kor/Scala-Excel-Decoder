lazy val root = project
  .in(file("."))
  .settings(
    ThisBuild / scalaVersion := "2.11.12",
    ThisBuild / version := "0.1.0-SNAPSHOT",
    ThisBuild / organization := "com.github.e8kor",
    ThisBuild / organizationName := "e8kor"
  )
  .aggregate(excel)
  .dependsOn(excel)

lazy val excel = project
  .in(file("core/excel"))
  .settings(
    sourceGenerators in Compile += compile.boilerplate.taskValue
  )
  .settings(compile.settings: _*)
  .settings(publish.settings: _*)
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
