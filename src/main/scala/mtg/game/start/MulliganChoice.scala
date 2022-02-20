package mtg.game.start

import mtg.core.PlayerId
import mtg.game.state.history.LogEvent
import mtg.game.state.{Choice, ExecutableGameAction, GameAction, GameState, PartialGameActionResult}

case class TakeMulligan(playerToAct: PlayerId, numberOfMulligansTakenSoFar: Int) extends ExecutableGameAction[MulliganDecision] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[MulliganDecision] = {
    PartialGameActionResult.ChildWithCallback(
      MulliganChoice(playerToAct, numberOfMulligansTakenSoFar),
      logResult)
  }

  def logResult(decision: MulliganDecision, gameState: GameState): PartialGameActionResult[MulliganDecision] = {
    val logEvent = decision match {
      case _: MulliganDecision.Mulligan =>
        LogEvent.Mulligan(playerToAct, gameState.gameData.startingHandSize - numberOfMulligansTakenSoFar - 1)
      case _: MulliganDecision.Keep =>
        LogEvent.KeepHand(playerToAct, gameState.gameData.startingHandSize - numberOfMulligansTakenSoFar)
    }
    PartialGameActionResult.childThenValue(logEvent, decision)(gameState)
  }
}

case class MulliganChoice(playerToAct: PlayerId, numberOfMulligansTakenSoFar: Int) extends Choice.WithParser[MulliganDecision] {
  override def getParser()(implicit gameState: GameState): PartialFunction[String, MulliganDecision] = {
    case "K" =>
      MulliganDecision.Keep(playerToAct)
    case "M" =>
      MulliganDecision.Mulligan(playerToAct)
  }
}
