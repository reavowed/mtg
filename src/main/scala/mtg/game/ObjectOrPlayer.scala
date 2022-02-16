package mtg.game

import net.reavowed.utils.ValueWrapper

sealed trait ObjectOrPlayer

case class ObjectId(value: Int) extends ValueWrapper[Int] with ObjectOrPlayer
case class PlayerId(value: String)  extends ValueWrapper[String] with ObjectOrPlayer
