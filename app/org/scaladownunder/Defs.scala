package org.scaladownunder

import datomisca._

import scala.util.Try


object Defs {

  type Transactions = Try[(DId, Seq[TxData])]

  type DBQuery[T] = Database => Try[T]

  type EntityQuery = DBQuery[Entity]

  type EntityIterableQuery = DBQuery[Iterable[Entity]]

  type DBWriter = Database => Transactions

  type EntityWithId = (Long, Entity)

}