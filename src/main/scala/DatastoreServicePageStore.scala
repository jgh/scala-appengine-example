import com.google.appengine.api.datastore.{Text, Entity, Query, DatastoreServiceFactory}
import com.google.appengine.api.memcache.MemcacheServiceFactory
import compat.Platform

trait DatastoreServicePageStore extends PageStore {
  lazy val datastore = DatastoreServiceFactory.getDatastoreService();
  val kind:String = "Page"

  class PageEntity(key:String) extends Page {
    val query = new Query(kind)
    query.addFilter("key", Query.FilterOperator.EQUAL, key)
    val preparedQuery = datastore.prepare(query)

    lazy val entity:Entity =  preparedQuery.asSingleEntity
    lazy val etag = getProperty("etag")
    lazy val source = getProperty("textile")
    lazy val content = source.map(TextilePageGenerator(key))

    def matchesEtag(etag: String): Boolean = this.etag.isDefined

   def getProperty(name: String): Option[String] = {
      if (entity == null) {
        None
      } else {
        entity.getProperty(name) match {
          case value: Text => Some(value.getValue);
          case value: String => Some(value);
          case null => None
        }
      }
    }

  }
  def getSource(key:String):Option[String] = getPage(key).source
  def putSource(key:String, source:String) {
    val page = getPageEntity(key)
    val entity = page.entity match {
      case null => {
        val e = new Entity(kind)
        e.setProperty("key", key)
        e
      }
      case _ => page.entity
    }
    entity.setProperty("textile",  new Text(source))
    entity.setProperty("etag", Platform.currentTime.toString)
    datastore.put(entity)
  }

  def getPageEntity(key: String): PageEntity =  new PageEntity( key)
  def getPage(key: String): Page = getPageEntity(key)

}