package mtg.instructions.verbs

import mtg.actions.moveZone.MoveToBattlefieldAction
import mtg.core.ObjectId
import mtg.effects.EffectContext
import mtg.game.state.history.HistoryEvent
import mtg.game.state.history.HistoryEvent.ResolvedAction
import mtg.game.state.{GameAction, GameState}
import mtg.instructions.nounPhrases.IndefiniteNounPhrase
import mtg.instructions.{IntransitiveEventMatchingVerb, Verb}

object EntersTheBattlefield extends Verb.WithSuffix(Verb.Enter, "the battlefield") with IntransitiveEventMatchingVerb[ObjectId] {
  override def matchesEvent(
    eventToMatch: HistoryEvent.ResolvedAction[_],
    gameState: GameState,
    effectContext: EffectContext,
    subjectPhrase: IndefiniteNounPhrase[ObjectId]
  ): Boolean = eventToMatch match {
    case ResolvedAction(MoveToBattlefieldAction(_, _), Some(objectId: ObjectId), _)
      if subjectPhrase.describes(objectId, gameState, effectContext)
    =>
      true
    case _ =>
      false
  }
}
