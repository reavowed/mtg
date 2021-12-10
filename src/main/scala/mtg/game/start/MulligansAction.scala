package mtg.game.start

import mtg._
import mtg.events.shuffle.ShuffleHandIntoLibrary
import mtg.game.PlayerId
import mtg.game.state._

case class MulligansAction(playersToMakeMulliganDecision: Seq[PlayerId], numberOfMulligansTakenSoFar: Int) extends RootGameAction {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[RootGameAction] = {
      PartialGameActionResult.childrenWithCallback[RootGameAction, Unit](
        playersToMakeMulliganDecision.map(DrawOpeningHandAction),
        mulliganChoices(_)(_))
  }

  def mulliganChoices(any: Any)(implicit gameState: GameState): PartialGameActionResult[RootGameAction] = {
    if (numberOfMulligansTakenSoFar < gameState.gameData.startingHandSize)
      PartialGameActionResult.childrenWithCallback[RootGameAction, MulliganDecision](
        playersToMakeMulliganDecision.map(MulliganChoice(_, numberOfMulligansTakenSoFar)),
        handleMulliganResult(_)(_))
    else
      PartialGameActionResult.childrenThenValue(
        playersToMakeMulliganDecision.map(ReturnCardsToLibraryChoice(_, gameState.gameData.startingHandSize)),
        TakeTurnAction.first(gameState))
  }

  def handleMulliganResult(decisions: Seq[MulliganDecision])(implicit gameState: GameState): PartialGameActionResult[RootGameAction] = {
    val playersMulliganning = decisions.ofType[MulliganDecision.Mulligan].map(_.player)
    if (playersMulliganning.nonEmpty) {
      PartialGameActionResult.childThenValue(
        WrappedOldUpdates(playersMulliganning.map(ShuffleHandIntoLibrary): _*),
        MulligansAction(playersMulliganning, numberOfMulligansTakenSoFar + 1))
    } else {
      TakeTurnAction.first(gameState)
    }
  }
}

object MulligansAction {
  def initial(implicit gameState: GameState): MulligansAction = MulligansAction(gameState.gameData.playersInTurnOrder, 0)
}









