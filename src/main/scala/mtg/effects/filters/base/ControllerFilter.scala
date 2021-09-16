package mtg.effects.filters.base

import mtg.effects.EffectContext
import mtg.effects.filters.PartialFilter
import mtg.effects.identifiers.StaticIdentifier
import mtg.game.state.GameState
import mtg.game.{ObjectId, PlayerId}

case class ControllerFilter(playerIdentifier: StaticIdentifier[PlayerId]) extends PartialFilter[ObjectId] {
  override def matches(t: ObjectId, effectContext: EffectContext, gameState: GameState): Boolean = {
    val expectedController = playerIdentifier.get(effectContext, gameState)
    gameState.gameObjectState.derivedState.stackObjectStates.get(t).map(_.controller)
      .orElse(gameState.gameObjectState.derivedState.permanentStates.get(t).map(_.controller))
      .contains(expectedController)
  }
  override def getText(cardName: String): String = playerIdentifier.getText(cardName) + " control"
}
