import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.memcache.MemcacheServiceFactory
import java.util.logging.Logger
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

class TextileServlet extends HttpServlet
//  with ServletResourcePageStore
  with DatastoreServicePageStore
  with PageStoreCache
{

  lazy val cache = MemcacheServiceFactory.getMemcacheService;
  lazy val datastore = DatastoreServiceFactory.getDatastoreService();

  val log:Logger  = Logger.getLogger("TextileServlet");

  override def doPost(request: HttpServletRequest, response: HttpServletResponse) = {
    doGet(request, response)
  }

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) = {
    //   val key = "/README.textile"
    val key = request.getServletPath.replace(".html", "")
    val result: Page = getPage(key)
    val clientETag = request.getHeader("If-None-Match")
    if (clientETag != null && result.matchesEtag(clientETag)) {
      //Browser will display it's cached version.
      response.sendError(304, "Not modified")
    } else {
      result.content match {
        case Some(content) => {
          response.setContentType("text/html")
          result.etag.foreach(response.setHeader("ETag", _))
          response.getWriter.write(content)
        }
//        case None => response.sendError(404, "No content found for key: " + key)
        case None => response.sendRedirect("/edit.html?key=" +key)
      }
    }
    ()
  }
}