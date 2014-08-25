package org.scaladownunder

import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.bson.BSONObjectID

import scala.concurrent.Future

case class Document(title: String, content: String)

object DocumentController extends Controller with MongoController {

  def collection: JSONCollection = db.collection[JSONCollection]("docs")

  def getInterestingDocuments = Action.async { request =>
    val wsSource1 = WS.url("http://wssource1").get
    val wsSource2 = WS.url("http://wssource2").get

    for {
      source1 <- wsSource1.map(parseJson)
      source2 <- wsSource2.map(parseJson)
      docs <- collection.find(Json.obj("title" -> "best docs")).cursor.collect[List]()
    } yield Ok(Json.toJson(combineData(source1, source2, docs)))

  }

  def createAndIndex = Action.async(parse.json) { request =>
    val json = request.body

    json.validate[Document].map { doc =>

      for {
        lastError <- collection.insert(doc)
        if lastError.ok
        _ <- WS.url("http://elasticsearch").post(Json.stringify(json))
      } yield Ok

    }.getOrElse{
      Future.successful(BadRequest("invalid json"))
    }

  }

  def parseJson(json: WSResponse): Seq[JsObject] = Seq(Json.obj("test data" -> true))

  def combineData(source1: Seq[JsObject], source2: Seq[JsObject], docs: List[Document]) = List(Document("title", "content"))

  implicit val docFormatter = Json.format[Document]

}

