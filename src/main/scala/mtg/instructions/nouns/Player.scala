package mtg.instructions.nouns

import mtg.core.PlayerId
import mtg.effects.EffectContext
import mtg.game.state.GameState

object Player extends ClassNoun[PlayerId] with Noun.RegularCaseObject {
  override def getAll(gameState: GameState, effectContext: EffectContext): Seq[PlayerId] = gameState.gameData.playersInTurnOrder
}
