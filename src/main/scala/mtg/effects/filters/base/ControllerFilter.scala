package mtg.effects.filters.base

import mtg.core.{ObjectId, PlayerId}
import mtg.effects.EffectContext
import mtg.effects.filters.PartialFilter
import mtg.game.state.GameState
import mtg.instructions.nounPhrases.StaticSingleIdentifyingNounPhrase
import mtg.text.{Sentence, Verb, VerbInflection, Verbs}

case class ControllerFilter(playerNoun: StaticSingleIdentifyingNounPhrase[PlayerId]) extends PartialFilter[ObjectId] {
  override def matches(t: ObjectId, gameState: GameState, effectContext: EffectContext): Boolean = {
    val expectedController = playerNoun.identify(gameState, effectContext)
    gameState.gameObjectState.derivedState.stackObjectStates.get(t).map(_.controller)
      .orElse(gameState.gameObjectState.derivedState.permanentStates.get(t).map(_.controller))
      .contains(expectedController)
  }
  override def getText(cardName: String): String = {
    playerNoun.getText(cardName) + " " + Verb.Control.inflect(VerbInflection.Present(playerNoun.person, playerNoun.number), cardName)
  }
}
