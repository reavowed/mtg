package mtg.game.start

import mtg._
import mtg.game.PlayerId
import mtg.game.state.{DirectChoice, GameState, PartialGameActionResult, WrappedOldUpdates}

case class ReturnCardsToLibraryChoice(playerToAct: PlayerId, numberOfCardsToReturn: Int) extends DirectChoice[Unit] {
  override def handleDecision(serializedChosenOption: String)(implicit gameState: GameState): Option[PartialGameActionResult[Unit]] = {
    val cardsInHand = gameState.gameObjectState.hands(playerToAct)
    for {
      ids <- serializedChosenOption.split(" ").toList.map(_.toIntOption).swap
      if ids.length == numberOfCardsToReturn
      cards <- ids.map(id => cardsInHand.find(_.objectId.sequentialId == id)).swap
    } yield PartialGameActionResult.child(WrappedOldUpdates(ReturnCardsToLibraryAction(playerToAct, cards)))
  }
}
