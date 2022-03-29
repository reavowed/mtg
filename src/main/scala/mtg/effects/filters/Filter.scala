package mtg.effects.filters

import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.TextComponent
import mtg.text.NounPhraseTemplate

trait PartialFilter[T <: ObjectOrPlayerId] extends TextComponent {
  def matches(t: T, gameState: GameState, effectContext: EffectContext): Boolean
}

trait Filter[T <: ObjectOrPlayerId] {
  def getSingular(cardName: String): String
  def getPlural(cardName: String): String = getSingular(cardName) + "s"
  def matches(t: T, gameState: GameState, effectContext: EffectContext): Boolean
  def getAll(gameState: GameState, effectContext: EffectContext): Set[T]
}

object Filter {
  implicit class ExtendedObjectFilter(objectFilter: Filter[ObjectId]) extends Filter[ObjectOrPlayerId] {
    override def getSingular(cardName: String): String = objectFilter.getSingular(cardName)
    override def getPlural(cardName: String): String = objectFilter.getPlural(cardName)
    override def matches(objectOrPlayer: ObjectOrPlayerId, gameState: GameState, effectContext: EffectContext): Boolean = {
      objectOrPlayer match {
        case objectId: ObjectId => objectFilter.matches(objectId, gameState, effectContext)
        case _: PlayerId => false
      }
    }
    override def getAll(gameState: GameState, effectContext: EffectContext): Set[ObjectOrPlayerId] = objectFilter.getAll(gameState, effectContext).recast[ObjectOrPlayerId]
  }
  implicit class ExtendedPlayerFilter(playerFilter: Filter[PlayerId]) extends Filter[ObjectOrPlayerId] {
    override def getSingular(cardName: String): String = playerFilter.getSingular(cardName)
    override def getPlural(cardName: String): String = playerFilter.getPlural(cardName)
    override def matches(objectOrPlayer: ObjectOrPlayerId, gameState: GameState, effectContext: EffectContext): Boolean = {
      objectOrPlayer match {
        case _: ObjectId => false
        case player: PlayerId => playerFilter.matches(player, gameState, effectContext)
      }
    }
    override def getAll(gameState: GameState, effectContext: EffectContext): Set[ObjectOrPlayerId] = playerFilter.getAll(gameState, effectContext).recast[ObjectOrPlayerId]
  }
}
