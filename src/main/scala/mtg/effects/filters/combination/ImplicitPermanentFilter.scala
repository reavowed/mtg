package mtg.effects.filters.combination

import mtg.core.ObjectId
import mtg.core.types.Type
import mtg.effects.EffectContext
import mtg.effects.filters.Filter
import mtg.game.state.GameState
import mtg.instructions.adjectives.Adjective
import mtg.instructions.nouns.{Noun, Permanent}

class ImplicitPermanentFilter(t: Type) extends Filter[ObjectId] {
  override def getSingular(cardName: String): String = t.name.toLowerCase
  override def getPlural(cardName: String): String = if (t == Type.Sorcery) "sorceries" else super.getPlural(cardName)
  override def getAll(gameState: GameState, effectContext: EffectContext): Seq[ObjectId] = {
    Noun.WithAdjective(Adjective.TypeAdjective(t), Permanent).getAll(gameState, effectContext)
  }
}
