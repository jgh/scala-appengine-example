import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

class EditServlet extends HttpServlet
  with DatastoreServicePageStore  with PageStoreCache {

  override def doPost(request: HttpServletRequest, response: HttpServletResponse) = {
    val key = request.getParameter("key")
    val source = request.getParameter("source")
    putSource(key, source)
    getPage(key).content.foreach( response.getWriter.write(_))
  }

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) = {
    val key = request.getParameter("key")
    if (key == null) {
      response.sendError(404, "No key found")
      error("No key")
    }
    val page = getPage(key)
    val source =  page.source match {
      case Some(s) => s;
      case None => ""
    }
     val out =
   <html>
          <head>
            <title>Edit</title>
              <link rel="stylesheet" href="docbook-xsl.css" type="text/css"/>
          </head>
          <body>
          <form name="form1" method="post" >
            <input type="hidden" name="key" value={key}/>
            <textarea name="source"  rows="80" cols="120">
              {source}
            </textarea>
            <br/>
            <input type="submit" name="Submit" border="0" class="buttonblue" onmouseover="this.style.color='#fbe249';" onmouseout="this.style.color='#FFF';"/>
          </form>
          </body>
        </html>.toString
          response.getWriter.write(out)
    ()
  }
}