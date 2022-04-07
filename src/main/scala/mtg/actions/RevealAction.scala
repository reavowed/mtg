package mtg.actions

import mtg.core.{ObjectId, PlayerId}
import mtg.game.objects.GameObjectState
import mtg.game.state.history.LogEvent
import mtg.game.state.{CurrentCharacteristics, DirectGameObjectAction, GameState}

case class RevealAction(playerId: PlayerId, objectId: ObjectId) extends DirectGameObjectAction {
  override def execute(implicit gameState: GameState): GameObjectState = None
  override def canBeReverted: Boolean = false
  override def getLogEvent(gameState: GameState): Option[LogEvent] = Some(LogEvent.RevealCard(playerId, CurrentCharacteristics.getName(objectId, gameState)))
}
