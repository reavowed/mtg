package mtg.game.start

import mtg._
import mtg.core.PlayerId
import mtg.game.objects.GameObject
import mtg.game.state.{Choice, DelegatingGameAction, GameAction, GameState}

case class ReturnCardsToLibrary(playerToAct: PlayerId, numberOfCardsToReturn: Int) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    ChooseCardsInHand(playerToAct, numberOfCardsToReturn)
      .flatMap(ReturnCardsToLibraryAction(playerToAct, _))
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
