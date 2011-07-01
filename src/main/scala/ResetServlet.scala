import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.memcache.MemcacheServiceFactory
import io.Source
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import javax.servlet.{ServletContext, ServletConfig}

class ResetServlet extends HttpServlet
  with DatastoreServicePageStore with PageStoreCache {

  lazy val cache = MemcacheServiceFactory.getMemcacheService;
  lazy val datastore = DatastoreServiceFactory.getDatastoreService();

  var ctx: ServletContext = null
  override def init(config: ServletConfig) = {
    ctx = config.getServletContext
    super.init
  }

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) = {
    def addSource(key: String) = {
      val resource = ctx.getResource(key + ".textile")
      if (resource != null) {
        putSource(key, Source.fromURL(resource).mkString)
      } else {
        error("couldn't find key: " + key)
      }
    }

    val templates = List("/CreateProject", "/IntroducingTextile", "/README", "/README2", "/SimpleWebApplication", "/SomeScala", "/ToDo", "/index", "/CachingContent", "/DynamicContent")
    templates.foreach(addSource)

    val out =
      <html>
        <head>
          <title>Reset</title>
            <link rel="stylesheet" href="docbook-xsl.css" type="text/css"/>
        </head>
        <body>
          {templates.map(t => <p><a href={t + ".html"}>{t}</a></p>)}
            Done!
         </body>
      </html>.toString
    response.getWriter.write(out)
    ()
  }
}