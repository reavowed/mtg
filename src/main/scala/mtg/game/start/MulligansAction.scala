package mtg.game.start

import mtg._
import mtg.actions.shuffle.ShuffleHandIntoLibraryAction
import mtg.definitions.PlayerId
import mtg.game.state._
import mtg.game.turns.turnEvents.ExecuteTurn

case class MulligansAction(playersToMakeMulliganDecision: Seq[PlayerId], numberOfMulligansTakenSoFar: Int) extends RootGameAction {
  override def delegate(implicit gameState: GameState): GameAction[RootGameAction] = {
    for {
      _ <- playersToMakeMulliganDecision.map(DrawOpeningHandAction).traverse
      nextAction <- takeMulligans(playersToMakeMulliganDecision, numberOfMulligansTakenSoFar)
    } yield nextAction
  }

  def takeMulligans(playersToMakeMulliganDecision: Seq[PlayerId], numberOfMulligansTakenSoFar: Int)(implicit gameState: GameState): GameAction[RootGameAction] = {
    if (numberOfMulligansTakenSoFar < gameState.gameData.startingHandSize) {
      for {
        decisions <- playersToMakeMulliganDecision.map(TakeMulligan(_, numberOfMulligansTakenSoFar)).traverse
        playersKeeping = decisions.ofType[MulliganDecision.Keep].map(_.player)
        playersMulliganning = decisions.ofType[MulliganDecision.Mulligan].map(_.player)
        playersToReturnCards = if (numberOfMulligansTakenSoFar > 0) playersKeeping else Nil
        _ <- playersToReturnCards.map(ReturnCardsToLibrary(_, numberOfMulligansTakenSoFar)).traverse
        _ <- playersMulliganning.map(ShuffleHandIntoLibraryAction).traverse
      } yield {
        if (playersMulliganning.nonEmpty)
          MulligansAction(playersMulliganning, numberOfMulligansTakenSoFar + 1)
        else
          ExecuteTurn.first(gameState)
      }
    } else {
      for {
        _ <- playersToMakeMulliganDecision.map(ReturnCardsToLibrary(_, gameState.gameData.startingHandSize)).traverse
      } yield ExecuteTurn.first
    }
  }
}

object MulligansAction {
  def initial(implicit gameState: GameState): MulligansAction = MulligansAction(gameState.gameData.playersInTurnOrder, 0)
}









