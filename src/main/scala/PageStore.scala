
/**
 * Created by IntelliJ IDEA.
 * User: Jeremy
 * Date: 10/03/11
 * Time: 9:51 PM
 * To change this template use File | Settings | File Templates.
 */
trait Page {
  def content:Option[String]
  def etag:Option[String]

  /**
   * Check if the client's etag matches the currently stored etag. Without loading
   * the content from the cache or generating content again.
   */
  def matchesEtag(etag:String):Boolean
}

object EmptyPage extends Page {
  val content = None
  val etag = None
  def matchesEtag(etag:String) = false
}

trait PageStore {
  def getPage(key:String):Page
}