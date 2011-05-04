import com.google.appengine.api.memcache.MemcacheServiceFactory

/**
 * Created by IntelliJ IDEA.
 * User: Jeremy
 * Date: 10/03/11
 * Time: 9:53 PM
 * To change this template use File | Settings | File Templates.
 */

trait PageStoreCache extends PageStore {
  lazy val cache = MemcacheServiceFactory.getMemcacheService;
  def etagKey(key:String) = key + ".etag"
  def contentKey(key:String) = key + ".content"

  def generatePage(key:String) =  {
    val page = super.getPage(key)
    page.content.foreach(cache.put(contentKey(key), _))
    page.etag.foreach(cache.put(etagKey(key), _))
    page
  }
  abstract override def putSource(key: String, source:String) =  {
    cache.delete(etagKey(key))
    cache.delete(contentKey(key))
    super.putSource(key, source)
  }

  abstract override def getPage(key: String): Page = {
   new Page {
       lazy val generate:Page  = generatePage(key)

       def matchesEtag(clientEtag:String):Boolean =
         cache.get(etagKey(key)) match {
             case null =>  generate.matchesEtag(clientEtag);
             case matches:String if matches.equals(clientEtag) => true;
             case other:String  => false;
         }

       lazy val content = cache.get(contentKey(key)) match {
         case value:String => Some(value + "<b>from cache</b>");
         case null => generate.content
       }
       lazy val etag =  cache.get(etagKey(key)) match {
         case value:String => Some(value);
         case null => generate.etag
       }

       lazy val source = generate.source
     }
  }

}