package mtg.game.state

import mtg.game.start.StartGameAction
import mtg.game.state.GameEvent.ResolvedEvent
import mtg.game.{GameStartingData, PlayerIdentifier}

import scala.annotation.tailrec

class GameStateManager(private var currentGameState: GameState, private var pendingActions: Seq[GameAction]) {
  def gameState: GameState = currentGameState
  def nextActions: Seq[GameAction] = pendingActions
  def nextAction: GameAction = pendingActions.head

  def initialize(): Unit = {
    executeAutomaticActions()
  }

  @tailrec
  private def executeAutomaticActions(): Unit = pendingActions match {
    case (gameActionManager: GameActionManager) +: remainingActions =>
      updateState(executeGameActionManager(gameActionManager), remainingActions)
      executeAutomaticActions()
    case (gameObjectEvent: GameObjectEvent) +: remainingActions =>
      updateState(executeGameObjectEvent(gameObjectEvent), remainingActions)
      executeAutomaticActions()
    case _ =>
  }

  private def executeGameActionManager(gameActionManager: GameActionManager): (GameState, Seq[GameAction]) = {
    (currentGameState, gameActionManager.execute(currentGameState))
  }

  private def executeGameObjectEvent(gameObjectEvent: GameObjectEvent): (GameState, Seq[GameAction]) = {
    (gameObjectEvent.execute(currentGameState) match {
      case GameObjectEventResult.UpdatedGameObjectState(newGameObjectState) =>
        (currentGameState.updateGameObjectState(newGameObjectState), Nil)
      case GameObjectEventResult.SubEvents(newEvents) =>
        (currentGameState, newEvents)
      case GameObjectEventResult.Nothing =>
        (currentGameState, Nil)
    }).mapLeft(_.recordEvent(ResolvedEvent(gameObjectEvent)))
  }

  def handleDecision(serializedDecision: String, actingPlayer: PlayerIdentifier): Unit = pendingActions match {
    case (choice: Choice) +: remainingActions =>
      if (choice.playerToAct == actingPlayer) {
        updateState(choice.handleDecision(serializedDecision, currentGameState), remainingActions)
        executeAutomaticActions()
      }
    case _ =>
  }

  private def updateState(newState: (GameState, Seq[GameAction]), remainingActions: Seq[GameAction]) = {
      val (newGameState, newActions) = newState
      currentGameState = newGameState
      pendingActions = newActions ++ remainingActions
  }
}

object GameStateManager {
  def initial(gameStartingData: GameStartingData): GameStateManager = {
    val gameStateManager = new GameStateManager(GameState.initial(gameStartingData), Seq(StartGameAction))
    gameStateManager.initialize()
    gameStateManager
  }
}
