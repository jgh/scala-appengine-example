import compat.Platform
import java.util.logging.Logger
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

class TextileServlet extends HttpServlet
 // with ServletResourcePageStore
  with DatastoreServicePageStore
  with PageStoreCache
{

  val log:Logger  = Logger.getLogger("TextileServlet");

  override def doPost(request: HttpServletRequest, response: HttpServletResponse) = {
    log.info("do get")
    doGet(request, response)
  }

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) = {
    //   val key = "/README.textile"
    val key = request.getServletPath.replace(".html", "")
    val result: Page = getPage(key)
    val clientETag = request.getHeader("If-None-Match")
    if (clientETag != null && result.matchesEtag(clientETag)) {
      response.sendError(304, "Not modified")
    } else {
      result.content match {
        case Some(content) => {
          response.setContentType("text/html")
          result.etag.foreach(response.setHeader("ETag", _))
          response.getWriter.write(content)
        }
        case None => response.sendError(404, "No content found for key: " + key)
      }
    }
    ()
  }
}