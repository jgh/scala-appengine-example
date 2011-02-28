import sbt._
class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val sbtIdeaRepo = "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
  val sbtIdea = "com.github.mpeltonen" % "sbt-idea-plugin" % "0.3.0"
  lazy val appEngine = "net.stbbs.yasushi" % "sbt-appengine-plugin" % "2.1" from "http://cloud.github.com/downloads/Yasushi/sbt-appengine-plugin/sbt-appengine-plugin-2.1.jar" ;
}