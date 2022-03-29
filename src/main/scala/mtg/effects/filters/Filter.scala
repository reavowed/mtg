package mtg.effects.filters

import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.instructions.TextComponent
import mtg.instructions.nouns.Noun
import mtg.text.NounPhraseTemplate

trait PartialFilter[T <: ObjectOrPlayerId] extends TextComponent {
  def matches(t: T, gameState: GameState, effectContext: EffectContext): Boolean
}

trait Filter[T <: ObjectOrPlayerId] extends Noun[T] {
  def getSingular(cardName: String): String
  def describes(t: T, gameState: GameState, effectContext: EffectContext): Boolean
}

object Filter {
  implicit class ExtendedObjectFilter(objectFilter: Filter[ObjectId]) extends Filter[ObjectOrPlayerId] {
    override def getSingular(cardName: String): String = objectFilter.getSingular(cardName)
    override def getPlural(cardName: String): String = objectFilter.getPlural(cardName)
    override def describes(objectOrPlayer: ObjectOrPlayerId, gameState: GameState, effectContext: EffectContext): Boolean = {
      objectOrPlayer match {
        case objectId: ObjectId => objectFilter.describes(objectId, gameState, effectContext)
        case _: PlayerId => false
      }
    }
  }
  implicit class ExtendedPlayerFilter(playerFilter: Filter[PlayerId]) extends Filter[ObjectOrPlayerId] {
    override def getSingular(cardName: String): String = playerFilter.getSingular(cardName)
    override def getPlural(cardName: String): String = playerFilter.getPlural(cardName)
    override def describes(objectOrPlayer: ObjectOrPlayerId, gameState: GameState, effectContext: EffectContext): Boolean = {
      objectOrPlayer match {
        case _: ObjectId => false
        case player: PlayerId => playerFilter.describes(player, gameState, effectContext)
      }
    }
  }
}
