package mtg.effects.filters

import mtg.effects.EffectContext
import mtg.game.PlayerId
import mtg.game.state.GameState

object AnyPlayerFilter extends Filter[PlayerId] {
  override def matches(t: PlayerId, effectContext: EffectContext, gameState: GameState): Boolean = true
  override def getText(cardName: String): String = "player"
  override def getAll(effectContext: EffectContext, gameState: GameState): Set[PlayerId] = gameState.gameData.playersInTurnOrder.toSet
}
