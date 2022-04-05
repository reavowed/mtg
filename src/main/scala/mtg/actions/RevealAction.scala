package mtg.actions

import mtg.core.{ObjectId, PlayerId}
import mtg.game.state.history.LogEvent
import mtg.game.state.{CurrentCharacteristics, GameActionResult, GameState, GameObjectAction}

case class RevealAction(playerId: PlayerId, objectId: ObjectId) extends GameObjectAction {
  override def execute(gameState: GameState): GameActionResult = {}
  override def canBeReverted: Boolean = false
  override def getLogEvent(gameState: GameState): Option[LogEvent] = Some(LogEvent.RevealCard(playerId, CurrentCharacteristics.getName(objectId, gameState)))
}
