package mtg.game.start.mulligans

import mtg._
import mtg.events.MoveObjectEvent
import mtg.game.objects.GameObject
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, GameActionResult, TypedPlayerChoice}
import mtg.game.{PlayerId, Zone}

case class ReturnCardsToLibraryOption(cardsToReturn: Seq[GameObject])

case class ReturnCardsToLibraryChoice(playerToAct: PlayerId, numberOfCardsToReturn: Int) extends TypedPlayerChoice[ReturnCardsToLibraryOption] {
  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[ReturnCardsToLibraryOption] = {
    for {
      ids <- serializedChosenOption.split(" ").toList.map(_.toIntOption).swap
      cardObjects <- ids.map(id => currentGameState.gameObjectState.hands(playerToAct).find(_.objectId.sequentialId == id)).swap
    } yield ReturnCardsToLibraryOption(cardObjects)
  }
  override def handleDecision(chosenOption: ReturnCardsToLibraryOption, currentGameState: GameState): GameActionResult  = {
    (
      chosenOption.cardsToReturn.map(MoveObjectEvent(playerToAct, _, Zone.Library(playerToAct))),
      LogEvent.ReturnCardsToLibrary(playerToAct, numberOfCardsToReturn)
    )
  }
}
