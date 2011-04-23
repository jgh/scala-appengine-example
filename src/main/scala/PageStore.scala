
/**
 * Created by IntelliJ IDEA.
 * User: Jeremy
 * Date: 10/03/11
 * Time: 9:51 PM
 * To change this template use File | Settings | File Templates.
 */
trait Page {
  def source:Option[String]
  def content:Option[String]
  def etag:Option[String]

  /**
   * Check if the client's etag matches the currently stored etag. Without loading
   * the content from the cache or generating content again.
   */
  def matchesEtag(etag:String):Boolean
}

object EmptyPage extends Page {
  val source = None
  val content = None
  val etag = None
  def matchesEtag(etag:String) = false
}

trait PageStore {
  def putSource(key:String, source:String)
  def getPage(key:String):Page
}