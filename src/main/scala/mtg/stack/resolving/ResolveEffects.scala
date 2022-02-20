package mtg.stack.resolving

import mtg.effects.oneshot.OneShotEffectResult
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.game.state.{ExecutableGameAction, GameActionResult, GameState, InternalGameAction, PartialGameActionResult, WrappedOldUpdates}

case class ResolveEffects(allEffects: Seq[OneShotEffect], initialResolutionContext: StackObjectResolutionContext) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    executeEffects(allEffects, initialResolutionContext)
  }

  private def executeEffects(
    effects: Seq[OneShotEffect],
    resolutionContext: StackObjectResolutionContext)(
    implicit gameState: GameState
  ): PartialGameActionResult[Unit] = {
    effects match {
      case effect +: remainingEffects =>
        effect.resolve(gameState, resolutionContext) match {
          case OneShotEffectResult.Event(event, newResolutionContext) =>
            PartialGameActionResult.ChildWithCallback(
              WrappedOldUpdates(event),
              (_: Unit, gameState) => executeEffects(remainingEffects, newResolutionContext)(gameState))
          case OneShotEffectResult.Choice(choice) =>
            PartialGameActionResult.ChildWithCallback(
              ResolveEffectChoice(choice, remainingEffects),
              handleDecision(remainingEffects))
          case OneShotEffectResult.Log(logEvent, newResolutionContext) =>
            PartialGameActionResult.ChildWithCallback(
              logEvent,
              (_: Unit, gameState) => executeEffects(remainingEffects, newResolutionContext)(gameState))
        }
      case Nil =>
        ()
    }
  }

  private def handleDecision(
    remainingEffects: Seq[OneShotEffect])(
    decision: (Option[InternalGameAction], StackObjectResolutionContext),
    gameState: GameState
  ): PartialGameActionResult[Unit] = decision match {
    case (Some(action), newResolutionContext) =>
      PartialGameActionResult.ChildWithCallback(
        WrappedOldUpdates(action),
        (_: Unit, gameState) => executeEffects(remainingEffects, newResolutionContext)(gameState))
    case (None, newResolutionContext) =>
      executeEffects(remainingEffects, newResolutionContext)(gameState)
  }
}
