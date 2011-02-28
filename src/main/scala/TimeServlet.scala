import java.util.Date
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import net.liftweb.textile.TextileParser

class TimeServlet extends HttpServlet {

  override def doGet(request:HttpServletRequest, response:HttpServletResponse) = {
    response.setContentType("text/html")
    response.getWriter.write("TIME: " + new Date)

  }
}