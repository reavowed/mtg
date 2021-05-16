package mtg

import mtg.game.GameData
import mtg.game.objects.GameObjectState
import mtg.game.state.{GameAction, GameHistory, GameResult, GameState, GameStateManager}

abstract class SpecWithGameStateManager extends SpecWithGameObjectState {

  implicit class GameStateManagerOps(gameStateManager: GameStateManager) {
    def updateGameState(f: GameState => GameState): GameStateManager = {
      new GameStateManager(f(gameStateManager.currentGameState), gameStateManager.onStateUpdate)
    }
    def updateGameObjectState(f: GameObjectState => GameObjectState): GameStateManager = {
      updateGameState(_.updateGameObjectState(f(gameStateManager.currentGameState.gameObjectState)))
    }
  }

  def createGameStateManager(gameData: GameData, gameObjectState: GameObjectState, actions: Seq[GameAction]): GameStateManager = {
    new GameStateManager(GameState(gameData, gameObjectState, GameHistory.empty, actions), _ => {})
  }

  def createGameStateManager(gameData: GameData, gameObjectState: GameObjectState, action: GameAction): GameStateManager = {
    createGameStateManager(gameData, gameObjectState, Seq(action, GameResult.Tie))
  }

  def runAction(action: GameAction, gameData: GameData, gameObjectState: GameObjectState): GameState = {
    createGameStateManager(gameData, gameObjectState, action).currentGameState
  }
}
