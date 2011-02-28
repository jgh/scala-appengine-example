import io.Source
import java.util.Date
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import javax.servlet.{ServletContext, ServletConfig}
import net.liftweb.textile.TextileParser

class TextileServlet extends HttpServlet {
  var ctx:ServletContext = null

  override def init(config: ServletConfig) = {
    ctx = config.getServletContext
  }

  override def doGet(request:HttpServletRequest, response:HttpServletResponse) = {
    response.setContentType("text/html")
    val textile = Source.fromURL(ctx.getResource("/README.textile")).mkString
    val html = TextileParser.toHtml(textile)
    response.getWriter.write(html.toString)

  }
}