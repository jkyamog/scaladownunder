package org.scaladownunder

import java.util.{List => JList, ArrayList}
import scala.collection.JavaConverters._

class Dao {

  def create[T](model: T): T = {
    EntityManager.persist(model)
    model
  }

  def findAll[T]: Iterable[T] = {
    EntityManager.find.asScala
  }

}

object EntityManager { // fake entity manager
  def persist[T](t: T) {}

  def find[T]: JList[T] = {
    new ArrayList[T]()
  }
}
