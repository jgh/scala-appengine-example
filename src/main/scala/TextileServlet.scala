import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

class TextileServlet extends HttpServlet with ServletResourcePageStore  with PageStoreCache {

  override def doPost(request: HttpServletRequest, response: HttpServletResponse) = {
    doGet(request, response)
  }

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) = {
    //   val key = "/README.textile"
    val key = request.getServletPath.replace(".html", ".textile")
    val result: Page = getPage(key)
    val clientETag = request.getHeader("If-None-Match")
    if (result.matchesEtag(clientETag)) {
      response.sendError(304, "Not modified")
    } else {
      result.content match {
        case Some(content) => {
          response.setContentType("text/html")
          result.etag.foreach(response.setHeader("ETag", _))
          response.getWriter.write(content)
        }
        case None => response.sendError(404)
      }
    }
    ()
  }
}