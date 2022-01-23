package mtg.game.start

import mtg.game.PlayerId
import mtg.game.state.history.LogEvent
import mtg.game.state.{DirectChoice, GameAction, GameState, PartialGameActionResult}

case class MulliganChoice(playerToAct: PlayerId, numberOfMulligansTakenSoFar: Int) extends DirectChoice.WithParser[MulliganDecision] {
  override def getParser()(implicit gameState: GameState): PartialFunction[String, PartialGameActionResult[MulliganDecision]] = {
    case "K" =>
      PartialGameActionResult.childThenValue(
        LogEvent.KeepHand(playerToAct, gameState.gameData.startingHandSize - numberOfMulligansTakenSoFar),
        MulliganDecision.Keep(playerToAct))
    case "M" =>
      PartialGameActionResult.childThenValue(
        LogEvent.Mulligan(playerToAct, gameState.gameData.startingHandSize - numberOfMulligansTakenSoFar - 1),
        MulliganDecision.Mulligan(playerToAct))
  }
}
