import com.google.appengine.api.datastore._
import compat.Platform

trait DatastoreServicePageStore extends PageStore {
  def datastore: DatastoreService
  /**
   * An implementation of a Page backed by a DatastoreService entity.
   */
  class PageEntity(key: String) extends Page {
    val kind: String = "Page"
    //Get or create entity
    lazy val entity: Entity = {
      val query = new Query(kind)
      query.addFilter("key", Query.FilterOperator.EQUAL, key)
      val preparedQuery = datastore.prepare(query)
      preparedQuery.asSingleEntity match {
        case null => {
          val e = new Entity(kind)
          e.setProperty("key", key)
          e
        }
        case _@e => e
      }
    }

    lazy val etag = getPropertyAsOption("etag")
    lazy val source = getPropertyAsOption("textile")
    lazy val content = source.map(TextilePageGenerator(key))

    def setEtag(etag: String) = entity.setProperty("etag", etag )
    def setSource(source: String) = entity.setProperty("textile", new Text(source))
    def save(): Unit = datastore.put(entity)

    def matchesEtag(clientEtag: String): Boolean = {
      val tagQuery = new Query(kind)
      tagQuery.addFilter("key", Query.FilterOperator.EQUAL, key)
      tagQuery.addFilter("etag", Query.FilterOperator.EQUAL, clientEtag)
      val etagPreparedQuery = datastore.prepare(tagQuery)
      etagPreparedQuery.countEntities(FetchOptions.Builder.withLimit(1)) == 1
    }

    def getPropertyAsOption(name: String): Option[String] = {
      entity.getProperty(name) match {
        case value: Text => Some(value.getValue);
        case value: String => Some(value);
        case null => None
      }
    }
  }

  def putSource(key: String, source: String) {
    val pageEntity = new PageEntity(key)
    pageEntity.setSource(source);
    pageEntity.setEtag(Platform.currentTime.toString);
    pageEntity.save()
  }
  def getPage(key: String): Page = new PageEntity(key)
}