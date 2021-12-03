package mtg.game.start

import mtg.game.PlayerId
import mtg.game.state.{DirectChoice, GameState, NewGameActionResult}

case class MulliganChoice(playerToAct: PlayerId, numberOfMulligansTakenSoFar: Int) extends DirectChoice.WithParser[MulliganDecision] {
  override def getParser()(implicit gameState: GameState): PartialFunction[String, NewGameActionResult.Partial[MulliganDecision]] = {
    case "K" =>
      if (numberOfMulligansTakenSoFar > 0)
        NewGameActionResult.Delegated.valueAfterChild(
          MulliganDecision.Keep(playerToAct),
          ReturnCardsToLibraryChoice(playerToAct, numberOfMulligansTakenSoFar))
      else
        NewGameActionResult.Value(MulliganDecision.Keep(playerToAct))
    case "M" => NewGameActionResult.Value(MulliganDecision.Mulligan(playerToAct))
  }
}
