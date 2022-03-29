package mtg.instructions.nouns

import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.effects.filters.base.SpellFilter
import mtg.game.state.GameState

case object Spell extends Noun.RegularCaseObject[ObjectId] {
  override def describes(objectId: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    SpellFilter.matches(objectId, gameState, effectContext)
  }
}
