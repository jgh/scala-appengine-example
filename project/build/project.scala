import sbt._

class ScalaAppEngineProject(info: ProjectInfo) extends AppengineProject(info) with IdeaProject with JRebel {
  val liftTextile = "net.liftweb" %% "lift-textile" % "2.2"
}
