//import com.typesafe.sbt.SbtGit._
//import com.typesafe.sbt.sbtghpages.GhpagesPlugin.autoImport._
//import com.typesafe.sbt.site.SitePlugin.autoImport._
//import microsites._
//import microsites.MicrositeKeys._
//import sbt._
//import sbt.Keys._
//import sbtunidoc.BaseUnidocPlugin.autoImport._
//import sbtunidoc.ScalaUnidocPlugin.autoImport._
//
//object doc {
//  private val filter = "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.svg" | "*.js" | "*.swf" | "*.yml" | "*.md"
//  lazy val docSettings = Seq(
//    micrositeName := "circe",
//    micrositeDescription := "A JSON library for Scala powered by Cats",
//    micrositeAuthor := "Travis Brown",
//    micrositeHighlightTheme := "atom-one-light",
//    micrositeHomepage := "https://circe.github.io/circe/",
//    micrositeBaseUrl := "circe",
//    micrositeDocumentationUrl := "api",
//    micrositeGithubOwner := "circe",
//    micrositeGithubRepo := "circe",
//    micrositeExtraMdFiles := Map(file("CONTRIBUTING.md") -> ExtraMdFileConfig("contributing.md", "docs")),
//    micrositePalette := Map(
//      "brand-primary" -> "#5B5988",
//      "brand-secondary" -> "#292E53",
//      "brand-tertiary" -> "#222749",
//      "gray-dark" -> "#49494B",
//      "gray" -> "#7B7B7E",
//      "gray-light" -> "#E5E5E6",
//      "gray-lighter" -> "#F4F3F4",
//      "white-color" -> "#FFFFFF"
//    ),
//    micrositeConfigYaml := ConfigYml(yamlInline = s"""
//    |scalafiddle:
//    |  dependency: io.circe %%% circe-core % $scalaFiddleCirceVersion,io.circe %%% circe-generic % $scalaFiddleCirceVersion,io.circe %%% circe-parser % $scalaFiddleCirceVersion
//    """.stripMargin),
//    addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), micrositeDocumentationUrl),
//    ghpagesNoJekyll := true,
//    scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
//      "-groups",
//      "-implicits",
//      "-skip-packages",
//      "scalaz",
//      "-doc-source-url",
//      scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
//      "-sourcepath",
//      baseDirectory.in(LocalRootProject).value.getAbsolutePath,
//      "-doc-root-content",
//      (resourceDirectory.in(Compile).value / "rootdoc.txt").getAbsolutePath
//    ),
//    scalacOptions ~= {
//      _.filterNot(Set("-Yno-predef"))
//    },
//    git.remoteRepo := "git@github.com:circe/circe.git",
//    unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject -- inProjects(noDocProjects(scalaVersion.value): _*),
//    includeFilter in makeSite := filter
//  )
//}
