package mtg.actions

import mtg.core.{ObjectId, PlayerId}
import mtg.game.state.history.LogEvent
import mtg.game.state.{CurrentCharacteristics, GameActionResult, GameState, InternalGameAction}

case class RevealAction(playerId: PlayerId, objectId: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    LogEvent.RevealCard(playerId, CurrentCharacteristics.getName(objectId, gameState))
  }
  override def canBeReverted: Boolean = false
}
