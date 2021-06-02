package mtg.game.actions

import mtg.abilities.ActivatedAbilityDefinition
import mtg.effects.OneShotEffect
import mtg.effects.oneshot.{OneShotEffectChoice, OneShotEffectResolutionContext, OneShotEffectResult}
import mtg.game.{ObjectId, PlayerId, Zone}
import mtg.game.state.history.{GameEvent, LogEvent}
import mtg.game.state._

case class ResolveActivatedAbility(player: PlayerId, objectWithAbility: ObjectWithState, ability: ActivatedAbilityDefinition) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    val resolutionContext = OneShotEffectResolutionContext.initial(objectWithAbility.gameObject.objectId, player, Nil)
    ResolveEffects(ability.effectParagraph.effects, resolutionContext)
  }
}

case class ResolveEffects(effects: Seq[OneShotEffect], resolutionContext: OneShotEffectResolutionContext) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    effects match {
      case effect +: remainingEffects =>
        effect.resolve(gameState, resolutionContext) match {
          case OneShotEffectResult.Event(event, newResolutionContext) =>
            Seq(event, ResolveEffects(remainingEffects, newResolutionContext))
          case OneShotEffectResult.Choice(choice) =>
            ResolveEffectChoice(choice, remainingEffects)
          case OneShotEffectResult.Log(logEvent, newResolutionContext) =>
            (ResolveEffects(remainingEffects, newResolutionContext), logEvent)
        }
      case Nil =>
        ()
    }
  }
}

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
