package mtg.game.start

import mtg.events.MoveObjectEvent
import mtg.game.objects.GameObject
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.game.{PlayerId, Zone}

case class ReturnCardsToLibraryAction(player: PlayerId, cardsToReturn: Seq[GameObject]) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    (
      cardsToReturn.map(MoveObjectEvent(player, _, Zone.Library(player))),
      LogEvent.ReturnCardsToLibrary(player, cardsToReturn.size)
    )
  }

  override def canBeReverted: Boolean = false
}
