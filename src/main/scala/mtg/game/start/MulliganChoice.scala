package mtg.game.start

import mtg.game.PlayerId
import mtg.game.state.{DirectChoice, GameState, PartialGameActionResult}

case class MulliganChoice(playerToAct: PlayerId, numberOfMulligansTakenSoFar: Int) extends DirectChoice.WithParser[MulliganDecision] {
  override def getParser()(implicit gameState: GameState): PartialFunction[String, PartialGameActionResult[MulliganDecision]] = {
    case "K" =>
      if (numberOfMulligansTakenSoFar > 0)
        PartialGameActionResult.childThenValue(
          ReturnCardsToLibraryChoice(playerToAct, numberOfMulligansTakenSoFar),
          MulliganDecision.Keep(playerToAct))
      else
        MulliganDecision.Keep(playerToAct)
    case "M" =>
      MulliganDecision.Mulligan(playerToAct)
  }
}
