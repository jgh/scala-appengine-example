import sbt._

class ScalaAppEngineProject(info: ProjectInfo) extends AppengineProject(info) with IdeaProject with JRebel {
  val liftTextile = "net.liftweb" %% "lift-textile" % "2.2"

//  override def jrebelJvmOptions = List("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005")
}
