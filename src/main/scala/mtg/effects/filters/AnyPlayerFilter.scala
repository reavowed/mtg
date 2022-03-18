package mtg.effects.filters

import mtg.core.PlayerId
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.text.{NounPhraseTemplate, Nouns}

object AnyPlayerFilter extends Filter[PlayerId] {
  override def matches(t: PlayerId, gameState: GameState, effectContext: EffectContext): Boolean = true
  override def getNounPhraseTemplate(cardName: String): NounPhraseTemplate = Nouns.Player
  override def getAll(gameState: GameState, effectContext: EffectContext): Set[PlayerId] = gameState.gameData.playersInTurnOrder.toSet
}
