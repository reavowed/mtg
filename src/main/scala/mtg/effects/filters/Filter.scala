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

trait Filter[+T <: ObjectOrPlayerId] extends Noun[T]
