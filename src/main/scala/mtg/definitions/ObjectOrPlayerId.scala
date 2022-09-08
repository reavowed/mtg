package mtg.definitions

import mtg.utils.ValueWrapper

sealed trait ObjectOrPlayerId

case class ObjectId(value: Int) extends ValueWrapper[Int] with ObjectOrPlayerId
case class PlayerId(value: String)  extends ValueWrapper[String] with ObjectOrPlayerId
