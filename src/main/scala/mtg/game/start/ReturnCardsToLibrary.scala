package mtg.game.start

import mtg._
import mtg.actions.moveZone.MoveToLibraryAction
import mtg.core.PlayerId
import mtg.game.objects.GameObject
import mtg.game.state.history.LogEvent
import mtg.game.state.{Choice, DelegatingGameAction, GameAction, GameState}

case class ReturnCardsToLibrary(player: PlayerId, numberOfCardsToReturn: Int) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    ChooseCardsInHand(player, numberOfCardsToReturn).flatMap(returnCards)
  }

  def returnCards(cardsToReturn: Seq[GameObject]): GameAction[Unit] = {
    cardsToReturn.map(card => MoveToLibraryAction(card.objectId)).traverse
      .andThen(LogEvent.ReturnCardsToLibrary(player, cardsToReturn.size))
  }
}

case class ChooseCardsInHand(playerToAct: PlayerId, numberOfCards: Int) extends Choice[Seq[GameObject]] {
  override def handleDecision(serializedChosenOption: String)(implicit gameState: GameState): Option[Seq[GameObject]] = {
    val cardsInHand = gameState.gameObjectState.hands(playerToAct)
    val ids = serializedChosenOption.split(" ").toList
    for {
      cards <- ids.map(id => cardsInHand.find(_.objectId.toString == id)).swap
      if cards.length == numberOfCards
    } yield cards
  }
}
