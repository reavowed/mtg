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
  def matches(t: T, gameState: GameState, effectContext: EffectContext): Boolean
  def getAll(gameState: GameState, effectContext: EffectContext): Set[T]
  def getNounPhraseTemplate(cardName: String): NounPhraseTemplate
}

object Filter {
  implicit class ExtendedObjectFilter(objectFilter: Filter[ObjectId]) extends Filter[ObjectOrPlayerId] {
    override def matches(objectOrPlayer: ObjectOrPlayerId, gameState: GameState, effectContext: EffectContext): Boolean = {
      objectOrPlayer match {
        case objectId: ObjectId => objectFilter.matches(objectId, gameState, effectContext)
        case _: PlayerId => false
      }
    }
    override def getNounPhraseTemplate(cardName: String): NounPhraseTemplate = objectFilter.getNounPhraseTemplate(cardName)
    override def getAll(gameState: GameState, effectContext: EffectContext): Set[ObjectOrPlayerId] = objectFilter.getAll(gameState, effectContext).recast[ObjectOrPlayerId]
  }
  implicit class ExtendedPlayerFilter(playerFilter: Filter[PlayerId]) extends Filter[ObjectOrPlayerId] {
    override def matches(objectOrPlayer: ObjectOrPlayerId, gameState: GameState, effectContext: EffectContext): Boolean = {
      objectOrPlayer match {
        case _: ObjectId => false
        case player: PlayerId => playerFilter.matches(player, gameState, effectContext)
      }
    }
    override def getNounPhraseTemplate(cardName: String): NounPhraseTemplate = playerFilter.getNounPhraseTemplate(cardName)
    override def getAll(gameState: GameState, effectContext: EffectContext): Set[ObjectOrPlayerId] = playerFilter.getAll(gameState, effectContext).recast[ObjectOrPlayerId]
  }
}
