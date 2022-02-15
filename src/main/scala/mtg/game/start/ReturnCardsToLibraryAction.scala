package mtg.game.start

import mtg.events.moveZone.MoveToLibraryEvent
import mtg.game.PlayerId
import mtg.game.objects.GameObject
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

case class ReturnCardsToLibraryAction(player: PlayerId, cardsToReturn: Seq[GameObject]) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    (
      cardsToReturn.map(card => MoveToLibraryEvent(card.objectId)),
      LogEvent.ReturnCardsToLibrary(player, cardsToReturn.size)
    )
  }

  override def canBeReverted: Boolean = false
}
