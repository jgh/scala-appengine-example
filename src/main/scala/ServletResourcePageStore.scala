import io.Source
import net.liftweb.textile.TextileParser
import javax.servlet.http.HttpServlet
import javax.servlet.{ServletConfig, ServletContext}
import net.liftweb.textile.TextileParser.WikiURLInfo

/**
 * Created by IntelliJ IDEA.
 * User: Jeremy
 * Date: 10/03/11
 * Time: 9:53 PM
 * To change this template use File | Settings | File Templates.
 */

trait ServletResourcePageStore extends HttpServlet with PageStore {
  var ctx: ServletContext = null

  def putSource(key:String, source:String) = {
    ()
  }// error("Put not supported for this page store.")

  override def init(config: ServletConfig) = {
    ctx = config.getServletContext
    super.init
  }

  def getPage(key: String): Page = {

    val resource = ctx.getResource(key + ".textile")
    if (resource == null) {
      EmptyPage
    } else {
      new Page() {
        val source = Some( Source.fromURL(resource).mkString)
        lazy val content = source.map(TextilePageGenerator(key))

        val etag = Some(System.currentTimeMillis.toString)

        def matchesEtag(clientEtag: String): Boolean = {
          clientEtag != null && clientEtag == etag
        }
      }
    }
  }
}
