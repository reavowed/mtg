package mtg.effects.filters

import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}
import mtg.game.state.GameState

trait Filter[T <: ObjectOrPlayer] {
  def isValid(t: T, gameState: GameState): Boolean
  def text: String
}

object Filter {
  implicit class ExtendedObjectFilter(objectFilter: Filter[ObjectId]) extends Filter[ObjectOrPlayer] {
    override def isValid(objectOrPlayer: ObjectOrPlayer, gameState: GameState): Boolean = {
      objectOrPlayer match {
        case objectId: ObjectId => objectFilter.isValid(objectId, gameState)
        case _: PlayerId => false
      }
    }
    override def text: String = objectFilter.text
  }
  implicit class ExtendedPlayerFilter(playerFilter: Filter[PlayerId]) extends Filter[ObjectOrPlayer] {
    override def isValid(objectOrPlayer: ObjectOrPlayer, gameState: GameState): Boolean = {
      objectOrPlayer match {
        case _: ObjectId => false
        case player: PlayerId => playerFilter.isValid(player, gameState)
      }
    }
    override def text: String = playerFilter.text
  }
}
