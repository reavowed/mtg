package mtg.game.actions

import mtg.abilities.ActivatedAbilityDefinition
import mtg.effects.{Effect, EffectChoice, EffectResult, ResolutionContext}
import mtg.game.PlayerIdentifier
import mtg.game.state.history.{GameEvent, LogEvent}
import mtg.game.state.{GameAction, GameState, InternalGameAction, InternalGameActionResult, ObjectWithState, PlayerChoice}

case class ResolveActivatedAbility(source: ObjectWithState, ability: ActivatedAbilityDefinition) extends InternalGameAction {
  override def execute(gameState: GameState): InternalGameActionResult = {
    val resolutionContext = ResolutionContext.initial(source)
    ResolveEffects(ability.effectParagraph.effects, resolutionContext)
  }
}

case class ResolveEffects(effects: Seq[Effect], resolutionContext: ResolutionContext) extends InternalGameAction {
  override def execute(gameState: GameState): InternalGameActionResult = {
    effects match {
      case effect +: remainingEffects =>
        effect.resolve(gameState, resolutionContext) match {
          case EffectResult.Event(event) =>
            Seq(event, ResolveEffects(remainingEffects, resolutionContext))
          case EffectResult.Choice(choice) =>
            ResolveEffectChoice(choice, remainingEffects)
          case EffectResult.Log(logEvent) =>
            (ResolveEffects(remainingEffects, resolutionContext), logEvent)
        }
      case Nil =>
        ()
    }
  }
}

case class ResolveEffectChoice(effectChoice: EffectChoice, remainingEffects: Seq[Effect]) extends PlayerChoice {
  override def playerToAct: PlayerIdentifier = effectChoice.playerChoosing
  override def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(GameEvent.Decision, Seq[GameAction], Option[LogEvent])] = {
    effectChoice.handleDecision(serializedDecision, currentGameState)
      .map { case (decision, newResolutionContext) =>
        (GameEvent.Decision(decision, playerToAct), Seq(ResolveEffects(remainingEffects, newResolutionContext)), None)
      }
  }
}
