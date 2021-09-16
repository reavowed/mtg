package mtg.game.stack

import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class ResolveEffects(effects: Seq[OneShotEffect], resolutionContext: StackObjectResolutionContext) extends InternalGameAction {
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
  override def canBeReverted: Boolean = true
}
