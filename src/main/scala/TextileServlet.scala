import com.google.appengine.api.memcache.{MemcacheServiceFactory, MemcacheService}
import io.Source
import java.io.{IOException, Serializable}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import javax.servlet.{ServletContext, ServletConfig}
import net.liftweb.textile.TextileParser
import xml.NodeSeq

trait Page[V] {
  def content:Option[V]
  def etag:Option[String]
  def matchesEtag(etag:String):Boolean
}

class TextileServlet extends HttpServlet {
  var ctx:ServletContext = null
  lazy val cache = MemcacheServiceFactory.getMemcacheService;


  override def init(config: ServletConfig) = {
    ctx = config.getServletContext
  }

  def getContent(key: String): Option[String]= {

    val resource = ctx.getResource(key)
    if(resource == null) {
      None
    } else {
      val textile = Source.fromURL(resource).mkString
      Some(<html>
        <head>
          <title>Readme</title>
          <link rel="stylesheet" href="docbook-xsl.css" type="text/css"/>
        </head>
        <body>{TextileParser.toHtml(textile)}</body>
      </html>.toString)
    }
  }

  def getCachedItem[K,V](key:K, generator: K => Option[V]):Page[V] = {
     new Page[V] {
       val etagKey = key + ".etag"
       val contentKey = key + ".content"
       def generate():Option[Tuple2[V, String]]  = generator(key).map(value => {
        val etag = System.currentTimeMillis.toString
        cache.put(contentKey, value)
        cache.put(etagKey, etag)
        (value, etag)
       })

       def matchesEtag(etag:String):Boolean = {etag != null && cache.get(etagKey) == etag}

       lazy val data:Option[Tuple2[V, String]] = cache.get(contentKey) match {
         case value:V => Some((value, cache.get(etagKey).asInstanceOf[String]))
         case null => generate();
         case _ => cache.delete(key); generate();
       }

       lazy val content = data.map(_._1 )
       lazy val etag =  data.map(_._2 )
     }
  }

  override def doPost(request:HttpServletRequest, response:HttpServletResponse) = {
    doGet(request, response )
  }
  override def doGet(request:HttpServletRequest, response:HttpServletResponse) = {
    val key = "/README.textile"

    val result:Page[String] = if(request.getParameter("nocache") == "true") {
      new Page[String]() {
        val content = getContent(key)
        val etag = None
        def matchesEtag(e:String) = false
      }
    } else {
      getCachedItem(key, getContent)
    }
    val clientETag = request.getHeader("If-None-Match")
    if (result.matchesEtag(clientETag))  { 1299331273496
      response.sendError(304, "Not modified")
    } else  {
     result.content match {
       case Some(content) => {
        response.setContentType("text/html")
        result.etag.foreach( response.setHeader("ETag", _))
        response.getWriter.write(content)
       }
       case None => response.sendError(404)
     }
    }
    ()
  }
}