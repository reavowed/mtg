package mtg.effects.filters

import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}
import mtg.game.state.GameState

trait PartialFilter[T <: ObjectOrPlayer] {
  def matches(t: T, gameState: GameState): Boolean
  def getText(cardName: String): String
}

trait Filter[T <: ObjectOrPlayer] extends PartialFilter[T] {
  def isValid(t: T, gameState: GameState): Boolean
  override def matches(t: T, gameState: GameState): Boolean = isValid(t, gameState)
}

object Filter {
  implicit class ExtendedObjectFilter(objectFilter: Filter[ObjectId]) extends Filter[ObjectOrPlayer] {
    override def isValid(objectOrPlayer: ObjectOrPlayer, gameState: GameState): Boolean = {
      objectOrPlayer match {
        case objectId: ObjectId => objectFilter.isValid(objectId, gameState)
        case _: PlayerId => false
      }
    }
    override def getText(cardName: String): String = objectFilter.getText(cardName)
  }
  implicit class ExtendedPlayerFilter(playerFilter: Filter[PlayerId]) extends Filter[ObjectOrPlayer] {
    override def isValid(objectOrPlayer: ObjectOrPlayer, gameState: GameState): Boolean = {
      objectOrPlayer match {
        case _: ObjectId => false
        case player: PlayerId => playerFilter.isValid(player, gameState)
      }
    }
    override def getText(cardName: String): String = playerFilter.getText(cardName)
  }
}
