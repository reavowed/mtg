package mtg.game.stack

import mtg.effects.OneShotEffect
import mtg.effects.oneshot.OneShotEffectChoice
import mtg.game.state.history.GameEvent
import mtg.game.state.{GameActionResult, GameState, PlayerChoice}
import mtg.game.{ObjectId, PlayerId, Zone}

case class ResolveEffectChoice(effectChoice: OneShotEffectChoice, remainingEffects: Seq[OneShotEffect]) extends PlayerChoice {
  override def playerToAct: PlayerId = effectChoice.playerChoosing

  override def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(GameEvent.Decision, GameActionResult)] = {
    effectChoice.handleDecision(serializedDecision, currentGameState)
      .map { case (decision, result, newResolutionContext) =>
        (GameEvent.Decision(decision, playerToAct), result.copy(childActions = result.childActions :+ ResolveEffects(remainingEffects, newResolutionContext)))
      }
  }

  override def temporarilyVisibleZones: Seq[Zone] = effectChoice.temporarilyVisibleZones

  override def temporarilyVisibleObjects: Seq[ObjectId] = effectChoice.temporarilyVisibleObjects
}
