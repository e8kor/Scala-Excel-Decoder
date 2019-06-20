lazy val root = project
  .in(file("."))
  .settings(
    skip in publish := true
  )
  .enablePlugins(MicrositesPlugin)
  .aggregate(excel)
  .dependsOn(excel)

lazy val excel = project
  .in(file("core/excel"))
  .settings(
    skip in publish := false,
    sourceGenerators in Compile += boilerplate.generate.taskValue
  )
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "org.typelevel" %% "cats-testkit" % "1.6.0" % Test
    ),
    libraryDependencies ++= Seq(
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.chuusai" %% "shapeless" % "2.3.3",
      "io.tmos" %% "arm4s" % "1.1.0",
      "org.typelevel" %% "cats-core" % "1.6.0",
      "org.apache.poi" % "poi-ooxml" % "4.1.0"
    )
  )
