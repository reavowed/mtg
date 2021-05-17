package mtg.game.start.mulligans

import mtg._
import mtg.events.MoveObjectEvent
import mtg.game.objects.CardObject
import mtg.game.state.history.LogEvent
import mtg.game.state.{ChoiceOption, GameAction, GameState, TypedChoice}
import mtg.game.{PlayerIdentifier, Zone}

case class ReturnCardsToLibraryOption(cardsToReturn: Seq[CardObject]) extends ChoiceOption

case class ReturnCardsToLibraryChoice(playerToAct: PlayerIdentifier, numberOfCardsToReturn: Int) extends TypedChoice[ReturnCardsToLibraryOption] {
  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[ReturnCardsToLibraryOption] = {
    val handCards = currentGameState.gameObjectState.hands(playerToAct).ofType[CardObject]
    for {
      ids <- serializedChosenOption.split(" ").toList.map(_.toIntOption).swap
      cardObjects <- ids.map(id => handCards.find(_.objectId.sequentialId == id)).swap
    } yield ReturnCardsToLibraryOption(cardObjects)
  }
  override def handleDecision(chosenOption: ReturnCardsToLibraryOption, currentGameState: GameState): (Seq[GameAction], Option[LogEvent])  = {
    (
      chosenOption.cardsToReturn.map(MoveObjectEvent(_, Zone.Library(playerToAct))),
      Some(LogEvent.ReturnCardsToLibrary(playerToAct, numberOfCardsToReturn))
    )
  }
}
