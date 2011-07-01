import com.google.appengine.api.memcache.MemcacheService

trait PageStoreCache extends PageStore {
  //Declare the dependency required by this trait. Any class using this trait must define a cache.
  def cache:MemcacheService
  def etagKey(key:String) = key + ".etag"
  def contentKey(key:String) = key + ".content"

  /**
   * Get page from super and put results into memcache.
   */
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
