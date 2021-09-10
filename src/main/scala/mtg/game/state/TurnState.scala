package mtg.game.state

import mtg.game.turns.{Turn, TurnPhase, TurnStep}

case class TurnState(
  currentTurnNumber: Int,
  currentTurn: Option[Turn],
  currentPhase: Option[TurnPhase],
  currentStep: Option[TurnStep])
{
  def startTurn(turn: Turn): TurnState = copy(currentTurn = Some(turn), currentPhase = None, currentStep = None, currentTurnNumber = currentTurnNumber + 1)
  def startPhase(phase: TurnPhase): TurnState = copy(currentPhase = Some(phase), currentStep = None)
  def startStep(step: TurnStep): TurnState = copy(currentStep = Some(step))
}

object TurnState {
  def initial: TurnState = TurnState(0, None, None, None)
}
