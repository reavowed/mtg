package mtg.instructions.verbs

import mtg.actions.moveZone.MoveToGraveyardAction
import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.game.state.GameState
import mtg.game.state.history.HistoryEvent
import mtg.game.state.history.HistoryEvent.ResolvedAction
import mtg.instructions.nounPhrases.IndefiniteNounPhrase
import mtg.instructions.{IntransitiveEventMatchingVerb, Verb}

case object Die extends Verb.RegularCaseObject with IntransitiveEventMatchingVerb[ObjectId] {
  override def looksBackInTime: Boolean = true
  override def matchesEvent(
    eventToMatch: HistoryEvent.ResolvedAction[_],
    gameState: GameState,
    effectContext: EffectContext,
    subjectPhrase: IndefiniteNounPhrase[ObjectId]
  ): Boolean = eventToMatch match {
    case ResolvedAction(MoveToGraveyardAction(objectId), Some(_), _)
      if subjectPhrase.describes(objectId, gameState, effectContext)
    =>
      true
    case _ =>
      false
  }
}
