package mtg.game.state

import mtg.game.PlayerIdentifier

import scala.annotation.tailrec

class GameStateManager(private var currentGameState: GameState) {
  def gameState: GameState = currentGameState

  def initialize(): Unit = {
    executeWhileAction()
  }

  @tailrec
  private def executeWhileAction(): Unit = currentGameState.nextTransition match {
    case action: Action =>
      currentGameState = action.runAction(currentGameState)
      executeWhileAction()
    case _ =>
  }

  def handleDecision(serializedDecision: String, actingPlayer: PlayerIdentifier): Unit = currentGameState.nextTransition match {
    case choice: Choice =>
      if (choice.playerToAct == actingPlayer) {
        currentGameState = choice.handleDecision(serializedDecision, currentGameState)
        executeWhileAction()
      }
    case _ =>
  }
}
