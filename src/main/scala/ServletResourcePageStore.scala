import io.Source
import net.liftweb.textile.TextileParser
import javax.servlet.http.HttpServlet
import javax.servlet.{ServletConfig, ServletContext}

/**
 * Created by IntelliJ IDEA.
 * User: Jeremy
 * Date: 10/03/11
 * Time: 9:53 PM
 * To change this template use File | Settings | File Templates.
 */

trait ServletResourcePageStore extends HttpServlet with PageStore {
  var ctx: ServletContext = null


  override def init(config: ServletConfig) = {
    ctx = config.getServletContext
    super.init
  }

  def getPage(key: String): Page = {
    val resource = ctx.getResource(key)
    if (resource == null) {
      EmptyPage
    } else {
      new Page() {
        lazy val content ={
          val textile = Source.fromURL(resource).mkString
          Some(<html>
          <head>
            <title>Readme</title>
              <link rel="stylesheet" href="docbook-xsl.css" type="text/css"/>
          </head>
          <body>{TextileParser.toHtml(textile)}</body>
        </html>.toString)
        }

        val etag = Some(System.currentTimeMillis.toString)

        def matchesEtag(clientEtag: String): Boolean = {
          clientEtag != null && clientEtag == etag
        }
      }
    }
  }


}