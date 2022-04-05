package mtg.game.start

import mtg.core.PlayerId
import mtg.game.state.history.LogEvent
import mtg.game.state.{Choice, DelegatingGameAction, GameAction, GameState}

case class TakeMulligan(playerToAct: PlayerId, numberOfMulligansTakenSoFar: Int) extends DelegatingGameAction[MulliganDecision] {
  override def delegate(implicit gameState: GameState): GameAction[MulliganDecision] = {
    for {
      decision <- MulliganChoice(playerToAct, numberOfMulligansTakenSoFar)
      _ <- decision match {
        case _: MulliganDecision.Mulligan =>
          LogEvent.Mulligan(playerToAct, gameState.gameData.startingHandSize - numberOfMulligansTakenSoFar - 1)
        case _: MulliganDecision.Keep =>
          LogEvent.KeepHand(playerToAct, gameState.gameData.startingHandSize - numberOfMulligansTakenSoFar)
      }
    } yield decision
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
