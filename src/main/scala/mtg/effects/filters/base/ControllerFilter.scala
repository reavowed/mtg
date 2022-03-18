package mtg.effects.filters.base

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.EffectContext
import mtg.effects.filters.PartialFilter
import mtg.effects.identifiers.StaticIdentifier
import mtg.game.state.GameState
import mtg.text.{Sentence, Verbs}

case class ControllerFilter(playerIdentifier: StaticIdentifier[PlayerId]) extends PartialFilter[ObjectId] {
  override def matches(t: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    val expectedController = playerIdentifier.get(gameState, effectContext)
    gameState.gameObjectState.derivedState.stackObjectStates.get(t).map(_.controller)
      .orElse(gameState.gameObjectState.derivedState.permanentStates.get(t).map(_.controller))
      .contains(expectedController)
  }
  override def getText(cardName: String): String = {
    Sentence.NounAndVerb(playerIdentifier.getNounPhrase(cardName), Verbs.Control).text
  }
}
