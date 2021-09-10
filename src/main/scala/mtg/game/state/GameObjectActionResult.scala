package mtg.game.state

import mtg.game.objects.GameObjectState

sealed abstract class GameObjectActionResult {
  def updateGameState(gameState: GameState): GameState
}
object GameObjectActionResult {
  case class UpdatedGameObjectState(gameObjectState: GameObjectState) extends GameObjectActionResult {
    override def updateGameState(gameState: GameState): GameState = gameState.updateGameObjectState(gameObjectState)
  }
  case class SubEvents(events: Seq[GameObjectAction]) extends GameObjectActionResult {
    override def updateGameState(gameState: GameState): GameState = gameState.addActions(events)
  }
  case object Nothing extends GameObjectActionResult {
    override def updateGameState(gameState: GameState): GameState = gameState
  }

  implicit def updatedGameObjectState(gameObjectState: GameObjectState): GameObjectActionResult = UpdatedGameObjectState(gameObjectState)
  implicit def optionalUpdatedGameObjectState(gameObjectStateOption: Option[GameObjectState]): GameObjectActionResult = gameObjectStateOption.map(UpdatedGameObjectState).getOrElse(Nothing)
  implicit def subEvent(event: GameObjectAction): GameObjectActionResult = SubEvents(Seq(event))
  implicit def optionalSubEvent(eventOption: Option[GameObjectAction]): GameObjectActionResult = eventOption.map(subEvent).getOrElse(Nothing)
  implicit def subEvents(events: Seq[GameObjectAction]): GameObjectActionResult = SubEvents(events)
  implicit def nothing(unit: Unit): GameObjectActionResult = Nothing
}
