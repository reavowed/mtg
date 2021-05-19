package mtg

import mtg.game.PlayerIdentifier
import mtg.game.objects.{Card, CardObject, GameObject, GameObjectState}
import mtg.game.state.history.GameHistory
import mtg.game.state.{GameAction, GameResult, GameState, GameStateManager}
import mtg.game.turns.{PriorityChoice, TurnPhase}
import org.specs2.matcher.Matcher

abstract class SpecWithGameStateManager extends SpecWithGameObjectState {

  def bePriorityForPlayer(player: PlayerIdentifier): Matcher[GameAction] = { (gameAction: GameAction) =>
    (gameAction.asOptionalInstanceOf[PriorityChoice].exists(_.playerToAct == player), "", "")
  }
  def beCardObject(card: Card): Matcher[GameObject] = { (gameObject: GameObject) =>
    (gameObject.asOptionalInstanceOf[CardObject].exists(_.card == card), "", "")
  }

  implicit class GameStateManagerOps(gameStateManager: GameStateManager) {
    def updateGameState(f: GameState => GameState): GameStateManager = {
      new GameStateManager(f(gameStateManager.currentGameState), gameStateManager.onStateUpdate)
    }
    def updateGameObjectState(f: GameObjectState => GameObjectState): GameStateManager = {
      updateGameState(_.updateGameObjectState(f(gameStateManager.currentGameState.gameObjectState)))
    }
    private def passUntil(predicate: GameState => Boolean): Unit = {
      while (!predicate(gameStateManager.currentGameState) && gameStateManager.currentGameState.pendingActions.head.isInstanceOf[PriorityChoice]) {
        gameStateManager.handleDecision("Pass", gameStateManager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].playerToAct)
      }
    }
    def passUntilTurn(turnNumber: Int): Unit = {
      passUntil(_.currentTurnNumber == turnNumber)
    }
    def passUntilPhase(turnPhase: TurnPhase): Unit = {
      passUntil(_.currentPhase.contains(turnPhase))
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
