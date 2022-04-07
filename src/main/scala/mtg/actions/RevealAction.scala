package mtg.actions

import mtg.core.{ObjectId, PlayerId}
import mtg.game.objects.GameObjectState
import mtg.game.state.history.LogEvent
import mtg.game.state.{CurrentCharacteristics, DirectGameObjectAction, GameState}

case class RevealAction(playerId: PlayerId, objectId: ObjectId) extends DirectGameObjectAction[Unit] {
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = ()
  override def canBeReverted: Boolean = false
  override def getLogEvent(gameState: GameState): Option[LogEvent] = Some(LogEvent.RevealCard(playerId, CurrentCharacteristics.getName(objectId, gameState)))
}
