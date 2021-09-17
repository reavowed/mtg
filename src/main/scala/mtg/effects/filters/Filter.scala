package mtg.effects.filters

import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

trait PartialFilter[T <: ObjectOrPlayer] {
  def matches(t: T, effectContext: EffectContext, gameState: GameState): Boolean
  def getText(cardName: String): String
}

trait Filter[T <: ObjectOrPlayer] {
  def matches(t: T, effectContext: EffectContext, gameState: GameState): Boolean
  def getAll(effectContext: EffectContext, gameState: GameState): Set[T]
  def getText(cardName: String): String
}

object Filter {
  implicit class ExtendedObjectFilter(objectFilter: Filter[ObjectId]) extends Filter[ObjectOrPlayer] {
    override def matches(objectOrPlayer: ObjectOrPlayer, effectContext: EffectContext, gameState: GameState): Boolean = {
      objectOrPlayer match {
        case objectId: ObjectId => objectFilter.matches(objectId, effectContext, gameState)
        case _: PlayerId => false
      }
    }
    override def getText(cardName: String): String = objectFilter.getText(cardName)
    override def getAll(effectContext: EffectContext, gameState: GameState): Set[ObjectOrPlayer] = objectFilter.getAll(effectContext, gameState).recast[ObjectOrPlayer]
  }
  implicit class ExtendedPlayerFilter(playerFilter: Filter[PlayerId]) extends Filter[ObjectOrPlayer] {
    override def matches(objectOrPlayer: ObjectOrPlayer, effectContext: EffectContext, gameState: GameState): Boolean = {
      objectOrPlayer match {
        case _: ObjectId => false
        case player: PlayerId => playerFilter.matches(player, effectContext, gameState)
      }
    }
    override def getText(cardName: String): String = playerFilter.getText(cardName)
    override def getAll(effectContext: EffectContext, gameState: GameState): Set[ObjectOrPlayer] = playerFilter.getAll(effectContext, gameState).recast[ObjectOrPlayer]
  }
}
