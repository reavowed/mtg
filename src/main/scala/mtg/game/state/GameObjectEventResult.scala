package mtg.game.state

import mtg.game.objects.GameObjectState

sealed abstract class GameObjectEventResult {
  def updateGameState(gameState: GameState): GameState
}
object GameObjectEventResult {
  case class UpdatedGameObjectState(gameObjectState: GameObjectState) extends GameObjectEventResult {
    override def updateGameState(gameState: GameState): GameState = gameState.updateGameObjectState(gameObjectState)
  }
  case class SubEvents(events: Seq[GameObjectEvent]) extends GameObjectEventResult {
    override def updateGameState(gameState: GameState): GameState = gameState.addActions(events)
  }
  case object Nothing extends GameObjectEventResult {
    override def updateGameState(gameState: GameState): GameState = gameState
  }

  implicit def updatedGameObjectState(gameObjectState: GameObjectState): GameObjectEventResult = UpdatedGameObjectState(gameObjectState)
  implicit def subEvent(event: GameObjectEvent): GameObjectEventResult = SubEvents(Seq(event))
  implicit def subEvents(events: Seq[GameObjectEvent]): GameObjectEventResult = SubEvents(events)
  implicit def nothing(unit: Unit): GameObjectEventResult = Nothing
}
