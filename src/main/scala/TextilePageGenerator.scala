import net.liftweb.textile.TextileParser
import net.liftweb.textile.TextileParser.WikiURLInfo

/**
 * Created by IntelliJ IDEA.
 * User: Jeremy
 * Date: 19/04/11
 * Time: 8:08 AM
 * To change this template use File | Settings | File Templates.
 */

object TextilePageGenerator {

  def apply(key:String)(textile:String):String = {
   <html>
          <head>
            <title>Readme</title>
              <link rel="stylesheet" href="docbook-xsl.css" type="text/css"/>
          </head>
          <body>{TextileParser.toHtml(textile, Some(DefaultRewriter()))}
          <p><a href={"edit.html?key=" +key}>edit</a></p></body>
        </html>.toString
  }


  case class DefaultRewriter() extends TextileParser.RewriteFunc {
    def apply(in: WikiURLInfo) = in match {
      case WikiURLInfo(word, _) =>
        ("/" + word + ".html", scala.xml.Text(word), None)
    }
  }
}