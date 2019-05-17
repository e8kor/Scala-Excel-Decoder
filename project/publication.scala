import com.typesafe.sbt.SbtPgp.autoImport._
import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin.autoImport._
import scala.xml.{ Elem, Node => XmlNode, NodeSeq => XmlNodeSeq }
import scala.xml.transform._

object publication extends AutoPlugin {

  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    releaseCrossBuild := true,
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    homepage := Some(url("https://github.com/e8kor/Scala-Excel-Decoder")),
    description := "Best Excel parser library for Scala you ever imagined",
    licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    startYear := Some(2019),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := (_ => false),
    autoAPIMappings := true,
    pomExtra := Seq(),
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
}
