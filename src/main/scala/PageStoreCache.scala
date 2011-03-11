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

  abstract override def getPage(key: String): Page = {
   val etagKey = key + ".etag"
   val contentKey = key + ".content"
   val generatePage = super.getPage(_)
   new Page {
       lazy val generate:Page  = {
         val page = generatePage(key)
         page.content.foreach(cache.put(contentKey, _))
         page.etag.foreach(cache.put(etagKey, _))
         page
       }

       def matchesEtag(etag:String):Boolean = {etag != null && (cache.get(etagKey) == etag || generate.matchesEtag(etag))}

       lazy val content = cache.get(contentKey) match {
         case value:String => Some(value + "<strong>from cache</strong>");
         case null => generate.content
       }
       lazy val etag =  cache.get(etagKey) match {
         case value:String => Some(value);
         case null => generate.etag
       }
     }
  }

}