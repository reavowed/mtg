package mtg.game.start.mulligans

import mtg._
import mtg.events.MoveObjectEvent
import mtg.game.objects.GameObject
import mtg.game.state.history.LogEvent
import mtg.game.state.{Choice, Decision, GameActionResult, GameState, InternalGameAction}
import mtg.game.{PlayerId, Zone}

case class ReturnCardsToLibraryChoice(playerToAct: PlayerId, numberOfCardsToReturn: Int, cardsInHand: Seq[GameObject]) extends Choice {
  override def parseDecision(serializedChosenOption: String): Option[Decision] = {
    for {
      ids <- serializedChosenOption.split(" ").toList.map(_.toIntOption).swap
      if ids.length == numberOfCardsToReturn
      cards <- ids.map(id => cardsInHand.find(_.objectId.sequentialId == id)).swap
    } yield ReturnCardsToLibrary(playerToAct, cards)
  }
}
object ReturnCardsToLibraryChoice {
  def apply(playerToAct: PlayerId, numberOfCardsToReturn: Int, gameState: GameState): ReturnCardsToLibraryChoice = {
    ReturnCardsToLibraryChoice(playerToAct, numberOfCardsToReturn, gameState.gameObjectState.hands(playerToAct))
  }
}

case class ReturnCardsToLibrary(player: PlayerId, cardsToReturn: Seq[GameObject]) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    (
      cardsToReturn.map(MoveObjectEvent(player, _, Zone.Library(player))),
      LogEvent.ReturnCardsToLibrary(player, cardsToReturn.size)
    )
  }
  override def canBeReverted: Boolean = false
}
