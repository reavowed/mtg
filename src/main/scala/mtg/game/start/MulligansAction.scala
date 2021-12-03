package mtg.game.start

import mtg._
import mtg.events.shuffle.ShuffleHandIntoLibrary
import mtg.game.PlayerId
import mtg.game.state._

case class MulligansAction(playersToMakeMulliganDecision: Seq[PlayerId], numberOfMulligansTakenSoFar: Int) extends RootGameAction {

  override def execute()(implicit gameState: GameState): NewGameActionResult.Partial[RootGameAction] = {
    if (numberOfMulligansTakenSoFar < gameState.gameData.startingHandSize)
      NewGameActionResult.Delegated.toChildren[RootGameAction, MulliganDecision](
        playersToMakeMulliganDecision.map(MulliganChoice(_, numberOfMulligansTakenSoFar)),
        handleMulliganResult(_)(_))
    else
      NewGameActionResult.Delegated.valueAfterChildren(
        TakeTurnAction.first(gameState),
        playersToMakeMulliganDecision.map(ReturnCardsToLibraryChoice(_, gameState.gameData.startingHandSize)))
  }

  def handleMulliganResult(decisions: Seq[MulliganDecision])(implicit gameState: GameState): NewGameActionResult.Partial[RootGameAction] = {
    val playersMulliganning = decisions.ofType[MulliganDecision.Mulligan].map(_.player)
    if (playersMulliganning.nonEmpty) {
      NewGameActionResult.Delegated.valueAfterChildren(
        MulligansAction(playersMulliganning, numberOfMulligansTakenSoFar + 1),
        playersMulliganning.flatMap(player => Seq(WrappedOldUpdates(ShuffleHandIntoLibrary(player)), DrawOpeningHandAction(player))))
    } else {
      NewGameActionResult.Value(TakeTurnAction.first(gameState))
    }
  }
}











