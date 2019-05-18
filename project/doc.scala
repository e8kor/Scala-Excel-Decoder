//import com.typesafe.sbt.SbtGit._
//import com.typesafe.sbt.sbtghpages.GhpagesPlugin.autoImport._
//import com.typesafe.sbt.site.SitePlugin.autoImport._
//import microsites._
import microsites.MicrositeKeys._
import microsites.MicrositesPlugin
import sbt._
//import sbt.Keys._
//import sbtunidoc.BaseUnidocPlugin.autoImport._
//import sbtunidoc.ScalaUnidocPlugin.autoImport._

object doc extends AutoPlugin {

//  private val filter = "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.svg" | "*.js" | "*.swf" | "*.yml" | "*.md"

  override def requires: Plugins = MicrositesPlugin

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    micrositeName := "Scala Excel Decoder",
    micrositeDescription := "A Excel parsing library for Scala",
    micrositeAuthor := "Eugene Korniichuk",
    micrositeHighlightTheme := "atom-one-light",
    micrositeHomepage := "https://e8kor.github.io/scala-excel-decoder/",
    micrositeBaseUrl := "scala-excel-decoder",
    micrositeDocumentationUrl := "api",
    micrositeGithubOwner := "e8kor",
    micrositeGithubRepo := "scala-excel-decoder",
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
//    git.remoteRepo := "git@github.com:e8kor/scala-excel-decoder.git",
//    unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject,
//    includeFilter in makeSite := filter
  )

}
