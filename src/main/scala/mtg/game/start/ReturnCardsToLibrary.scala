package mtg.game.start

import mtg._
import mtg.core.PlayerId
import mtg.game.objects.GameObject
import mtg.game.state.{DirectChoice, ExecutableGameAction, GameState, PartialGameActionResult, WrappedOldUpdates}

case class ReturnCardsToLibrary(playerToAct: PlayerId, numberOfCardsToReturn: Int) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    PartialGameActionResult.ChildWithCallback(
      ChooseCardsInHand(playerToAct, numberOfCardsToReturn),
      returnCards)
  }
  def returnCards(cards: Seq[GameObject], gameState: GameState): PartialGameActionResult[Unit] = {
    PartialGameActionResult.child(WrappedOldUpdates(ReturnCardsToLibraryAction(playerToAct, cards)))
  }
}

case class ChooseCardsInHand(playerToAct: PlayerId, numberOfCards: Int) extends DirectChoice[Seq[GameObject]] {
  override def handleDecision(serializedChosenOption: String)(implicit gameState: GameState): Option[Seq[GameObject]] = {
    val cardsInHand = gameState.gameObjectState.hands(playerToAct)
    val ids = serializedChosenOption.split(" ").toList
    for {
      cards <- ids.map(id => cardsInHand.find(_.objectId.toString == id)).swap
      if cards.length == numberOfCards
    } yield cards
  }
}
