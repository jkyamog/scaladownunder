package org.scaladownunder

import datomisca._
import datomisca.plugin.DatomiscaPlayPlugin

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import play.api.Play.current

import scala.concurrent.{Future, ExecutionContext}
import scala.util._

import Defs._

object PersonController extends Controller with DatomicDB {
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  import Person._

  def writeToDb(id: Long) = Action.async(parse.json) { request =>
    doDb {
      writerPersonToDb(request.body) andThen
        transact andThen
        getEntity(id)
    } map { entity =>
      Ok(Json.toJson(entity))
    }
  }


  def writerPersonToDb(json: JsValue): DBWriter = { db =>
    val id = DId(Partition.USER)

    readsPerson.reads(json) match {
      case JsSuccess((fName, sName), _) =>
        Success((id, Seq(
          (SchemaEntity.newBuilder
            +?= (firstName -> fName)
            +?= (surname -> sName)
            ) withId id
        )))
      case JsError(e) => Failure(new IllegalArgumentException(JsError.toFlatJson(e).toString))
    }
  }

  val readsPerson = (
    (__ \ 'firstName).readNullable[String] and
    (__ \ 'surname).readNullable[String]).tupled

  implicit val writesPerson = (
    (__ \ 'firstName).writeNullable[String] and
    (__ \ 'surname).writeNullable[String]
  )(unlift(unapply))

}


trait DatomicDB {
  self: Controller =>

  import Future.fromTry

  val uri = DatomiscaPlayPlugin.uri("mem")
  implicit val conn = Datomic.connect(uri)

  def doDb[A](dbBlock: Database => A): A = dbBlock.apply(conn.database)

  def resolveEntity(implicit ec: ExecutionContext): PartialFunction[Transactions, Future[EntityWithId]] = {
    case transactions =>
      for {
        (id, tx) <- fromTry(transactions)
        txReport <- conn.transact(tx)
      } yield (txReport.resolve(id), txReport.resolveEntity(id))
  }

  def transact(implicit ec: ExecutionContext): PartialFunction[Transactions, Future[Database]] = {
    case transactions =>
      for {
        (id, tx) <- fromTry(transactions)
        txReport <- conn.transact(tx)
      } yield txReport.dbAfter

  }

  def getEntity(id: Long)(implicit ec: ExecutionContext): PartialFunction[Future[Database], Future[Entity]] = {
    case fDb =>
      for {
        db <- fDb
      } yield db.entity(id)
  }

}


object Person {
  val ns = new Namespace("person")

  val firstName = Attribute(ns / "firstname", SchemaType.string, Cardinality.one)
  val surname = Attribute(ns / "surname", SchemaType.string, Cardinality.one)

  def unapply(entity: Entity): Option[(Option[String], Option[String])] = {
    Some(entity.readOpt(firstName), entity.readOpt(surname))
  }
}