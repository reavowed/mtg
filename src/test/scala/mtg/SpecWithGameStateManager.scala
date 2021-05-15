package mtg

import mtg.game.GameData
import mtg.game.objects.GameObjectState
import mtg.game.state.{GameAction, GameHistory, GameState, GameStateManager}

abstract class SpecWithGameStateManager extends SpecWithGameObjectState {

  implicit class GameStateManagerOps(gameStateManager: GameStateManager) {
    def updateGameState(f: GameState => GameState): GameStateManager = {
      new GameStateManager(f(gameStateManager.gameState), gameStateManager.nextActions)
    }
    def updateGameObjectState(f: GameObjectState => GameObjectState): GameStateManager = {
      updateGameState(_.updateGameObjectState(f(gameStateManager.gameState.gameObjectState)))
    }
  }

  def createGameStateManager(gameData: GameData, gameObjectState: GameObjectState, actions: Seq[GameAction]): GameStateManager = {
    val gameStateManager = new GameStateManager(GameState(gameData, gameObjectState, GameHistory.empty), actions)
    gameStateManager.initialize()
    gameStateManager
  }

  def createGameStateManager(gameData: GameData, gameObjectState: GameObjectState, action: GameAction): GameStateManager = {
    createGameStateManager(gameData, gameObjectState, Seq(action))
  }

  def runAction(action: GameAction, gameData: GameData, gameObjectState: GameObjectState): GameState = {
    createGameStateManager(gameData, gameObjectState, action).gameState
  }
}
