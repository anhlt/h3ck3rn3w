package base.db

import org.joda.time.DateTime
import slick.lifted
import com.github.tototoshi.slick.GenericJodaSupport

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

trait Repository[E <: Entity] {
  def all: Future[Seq[E]]

  def byId(id: Long): Future[Option[E]]

  def insert(entity: E): Future[E]

  def update(entity: E): Future[Int]

  def delete(id: Long): Future[Boolean]
}

trait TableDefinition {
  val dbConfiguration: DBConfiguration
  val db = dbConfiguration.db

  object jodaSupport extends GenericJodaSupport(dbConfiguration.driver)

  import dbConfiguration.driver.api._
  import slick.lifted._
  import jodaSupport._

  /**
    * The [[BaseTable]] describes the basic [[Entity]]
    */
  abstract class BaseTable[E <: Entity: ClassTag](
      tag: Tag,
      tableName: String,
      schemaName: Option[String] = None
  ) extends Table[E](tag, schemaName, tableName) {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    val createdAt = column[DateTime](
      "created_date",
      O.Default(DateTime.now())
    )
    val updatedAt = column[DateTime](
      "updated_date",
      O.Default(DateTime.now())
    )
  }
  /**
  * The root basic repository definition
  * The repo definition which should be used within an entity repo
  */
}

trait Repositories {
  val tableDefinition: TableDefinition
  implicit val ec: ExecutionContext

  trait SqlBaseRepo[E <: Entity, T <: TableDefinition#BaseTable[E]]
      extends Repository[E] {

    import tableDefinition.dbConfiguration.driver.api._

    val db: Database = tableDefinition.dbConfiguration.db

    def table: lifted.TableQuery[T]

    override def all: Future[Seq[E]] = db.run {
      table.to[Seq].result
    }

    override def byId(id: Long): Future[Option[E]] = db.run {
      table.filter(_.id === id).result.headOption
    }

    override def insert(entity: E): Future[E] = db.run {
      table returning table += entity
    }

    override def update(entity: E): Future[Int] = db.run {
      table.insertOrUpdate(entity)
    }

    override def delete(id: Long): Future[Boolean] = db.run {
      table.filter(_.id === id).delete.map(_ > 0)
    }

  }

}
