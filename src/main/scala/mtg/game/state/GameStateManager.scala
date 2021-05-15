package mtg.game.state

import mtg.game.start.StartGameAction
import mtg.game.{GameStartingData, PlayerIdentifier}

import scala.annotation.tailrec

class GameStateManager(private var currentGameState: GameState, private var pendingAction: GameAction) {
  def gameState: GameState = currentGameState
  def nextAction: GameAction = pendingAction

  def initialize(): Unit = {
    executeAutomaticActions()
  }

  @tailrec
  private def executeAutomaticActions(): Unit = pendingAction match {
    case automaticGameAction: AutomaticGameAction =>
      updateState(automaticGameAction.execute(currentGameState))
      executeAutomaticActions()
    case _ =>
  }

  def handleDecision(serializedDecision: String, actingPlayer: PlayerIdentifier): Unit = pendingAction match {
    case choice: Choice =>
      if (choice.playerToAct == actingPlayer) {
        updateState(choice.handleDecision(serializedDecision, currentGameState))
        executeAutomaticActions()
      }
    case _ =>
  }

  private def updateState(newState: (GameState, GameAction)) = {
      val (newGameState, newAction) = newState
      currentGameState = newGameState
      pendingAction = newAction
  }
}

object GameStateManager {
  def initial(gameStartingData: GameStartingData): GameStateManager = {
    val gameStateManager = new GameStateManager(GameState.initial(gameStartingData), StartGameAction)
    gameStateManager.initialize()
    gameStateManager
  }
}
