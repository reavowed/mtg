package mtg.game.start

import mtg._
import mtg.events.shuffle.ShuffleHandIntoLibrary
import mtg.game.PlayerId
import mtg.game.state._
import mtg.game.turns.turnEvents.ExecuteTurn

case class MulligansAction(playersToMakeMulliganDecision: Seq[PlayerId], numberOfMulligansTakenSoFar: Int) extends RootGameAction {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[RootGameAction] = {
      PartialGameActionResult.childrenWithCallback[RootGameAction, Unit](
        playersToMakeMulliganDecision.map(DrawOpeningHandAction),
        mulliganChoices)
  }

  def mulliganChoices(any: Any, gameState: GameState): PartialGameActionResult[RootGameAction] = {
    if (numberOfMulligansTakenSoFar < gameState.gameData.startingHandSize)
      PartialGameActionResult.childrenWithCallback[RootGameAction, MulliganDecision](
        playersToMakeMulliganDecision.map(MulliganChoice(_, numberOfMulligansTakenSoFar)),
        handleMulliganResult)(
        gameState)
    else
      PartialGameActionResult.childrenThenValue(
        playersToMakeMulliganDecision.map(ReturnCardsToLibrary(_, gameState.gameData.startingHandSize)),
        ExecuteTurn.first(gameState))(
        gameState)
  }

  def handleMulliganResult(decisions: Seq[MulliganDecision], gameState: GameState): PartialGameActionResult[RootGameAction] = {
    val playersKeeping = decisions.ofType[MulliganDecision.Keep].map(_.player)
    val playersMulliganning = decisions.ofType[MulliganDecision.Mulligan].map(_.player)
    val keepActions = if (numberOfMulligansTakenSoFar > 0) playersKeeping.map(ReturnCardsToLibrary(_, numberOfMulligansTakenSoFar)) else Nil
    val mulliganActions = playersMulliganning.map(p => WrappedOldUpdates(ShuffleHandIntoLibrary(p)))
    val resultAction = if (playersMulliganning.nonEmpty) MulligansAction(playersMulliganning, numberOfMulligansTakenSoFar + 1) else ExecuteTurn.first(gameState)
    PartialGameActionResult.childrenThenValue(
      keepActions ++ mulliganActions,
      resultAction)(
      gameState)
  }
}

object MulligansAction {
  def initial(implicit gameState: GameState): MulligansAction = MulligansAction(gameState.gameData.playersInTurnOrder, 0)
}









