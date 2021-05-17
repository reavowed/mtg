package mtg

import mtg.game.objects.GameObjectState
import mtg.game.state.history.GameHistory
import mtg.game.state.{GameAction, GameResult, GameState, GameStateManager}

abstract class SpecWithGameStateManager extends SpecWithGameObjectState {

  implicit class GameStateManagerOps(gameStateManager: GameStateManager) {
    def updateGameState(f: GameState => GameState): GameStateManager = {
      new GameStateManager(f(gameStateManager.currentGameState), gameStateManager.onStateUpdate)
    }
    def updateGameObjectState(f: GameObjectState => GameObjectState): GameStateManager = {
      updateGameState(_.updateGameObjectState(f(gameStateManager.currentGameState.gameObjectState)))
    }
  }

  def createGameState(gameObjectState: GameObjectState, actions: Seq[GameAction]): GameState = {
    GameState(gameData, gameObjectState, GameHistory.empty, actions)
  }

  def createGameStateManager(gameObjectState: GameObjectState, actions: Seq[GameAction]): GameStateManager = {
    new GameStateManager(createGameState(gameObjectState, actions), _ => {})
  }

  def createGameStateManager(gameObjectState: GameObjectState, action: GameAction): GameStateManager = {
    createGameStateManager(gameObjectState, Seq(action, GameResult.Tie))
  }

  def runAction(action: GameAction, gameObjectState: GameObjectState): GameState = {
    createGameStateManager(gameObjectState, action).currentGameState
  }
}
