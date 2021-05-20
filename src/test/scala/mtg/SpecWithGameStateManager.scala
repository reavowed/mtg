package mtg

import mtg.cards.CardDefinition
import mtg.game.PlayerIdentifier
import mtg.game.objects.{Card, CardObject, GameObject, GameObjectState}
import mtg.game.state.history.GameHistory
import mtg.game.state.{GameAction, GameResult, GameState, GameStateManager, ObjectWithState}
import mtg.game.turns.{PriorityChoice, TurnPhase}
import org.specs2.matcher.Matcher

abstract class SpecWithGameStateManager extends SpecWithGameObjectState {

  def bePriorityForPlayer(player: PlayerIdentifier): Matcher[GameAction] = { (gameAction: GameAction) =>
    (gameAction.asOptionalInstanceOf[PriorityChoice].exists(_.playerToAct == player), "", "")
  }
  def beCardObject(card: Card): Matcher[GameObject] = { (gameObject: GameObject) =>
    (gameObject.asOptionalInstanceOf[CardObject].exists(_.card == card), "", "")
  }
  def beObjectWithState(gameObject: GameObject): Matcher[ObjectWithState] = {(objectWithState: ObjectWithState) =>
    (objectWithState.gameObject == gameObject, "", "")
  }

  implicit class GameObjectSeqOps(gameObjects: Seq[GameObject]) {
    def getCard(cardDefinition: CardDefinition): CardObject = {
      gameObjects.ofType[CardObject].filter(_.card.printing.cardDefinition == cardDefinition).head
    }
  }

  implicit class GameStateManagerOps(gameStateManager: GameStateManager) {
    def updateGameState(f: GameState => GameState): GameStateManager = {
      new GameStateManager(f(gameStateManager.currentGameState), gameStateManager.onStateUpdate)
    }
    def updateGameObjectState(f: GameObjectState => GameObjectState): GameStateManager = {
      updateGameState(_.updateGameObjectState(f(gameStateManager.currentGameState.gameObjectState)))
    }
    def passPriority(player: PlayerIdentifier): Unit = {
      gameStateManager.handleDecision("Pass", player)
    }
    private def passUntil(predicate: GameState => Boolean): Unit = {
      while (!predicate(gameStateManager.currentGameState) && gameStateManager.currentGameState.pendingActions.head.isInstanceOf[PriorityChoice]) {
        passPriority(gameStateManager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].playerToAct)
      }
    }
    def passUntilTurn(turnNumber: Int): Unit = {
      passUntil(_.currentTurnNumber == turnNumber)
    }
    def passUntilPhase(turnPhase: TurnPhase): Unit = {
      passUntil(_.currentPhase.contains(turnPhase))
    }

    def playLand(land: GameObject, player: PlayerIdentifier): Unit = {
      gameStateManager.handleDecision("Play " + land.objectId.sequentialId, player)
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
