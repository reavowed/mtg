package mtg.game.start

import mtg._
import mtg.events.shuffle.ShuffleHandIntoLibrary
import mtg.game.PlayerId
import mtg.game.state._

case class MulligansAction(playersToMakeMulliganDecision: Seq[PlayerId], numberOfMulligansTakenSoFar: Int) extends RootGameAction {

  override def execute()(implicit gameState: GameState): PartialGameActionResult[RootGameAction] = {
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
      PartialGameActionResult.childrenThenValue(
        playersMulliganning.flatMap(player => Seq(WrappedOldUpdates(ShuffleHandIntoLibrary(player)), DrawOpeningHandAction(player))),
        MulligansAction(playersMulliganning, numberOfMulligansTakenSoFar + 1))
    } else {
      TakeTurnAction.first(gameState)
    }
  }
}











