package mtg.game.state

import mtg.game.objects.GameObjectState

sealed abstract class GameObjectEventResult {
  def updateGameState(gameState: GameState): (GameState, Seq[GameAction])
}
object GameObjectEventResult {
  case class UpdatedGameObjectState(gameObjectState: GameObjectState) extends GameObjectEventResult {
    override def updateGameState(gameState: GameState): (GameState, Seq[GameAction]) = (gameState.updateGameObjectState(gameObjectState), Nil)
  }
  case class SubEvents(events: Seq[GameObjectEvent]) extends GameObjectEventResult {
    override def updateGameState(gameState: GameState): (GameState, Seq[GameAction]) = (gameState, events)
  }
  case object Nothing extends GameObjectEventResult {
    override def updateGameState(gameState: GameState): (GameState, Seq[GameAction]) = (gameState, Nil)
  }

  implicit def updatedGameObjectState(gameObjectState: GameObjectState): GameObjectEventResult = UpdatedGameObjectState(gameObjectState)
  implicit def subEvents(events: Seq[GameObjectEvent]): GameObjectEventResult = SubEvents(events)
  implicit def nothing(unit: Unit): GameObjectEventResult = Nothing
}
