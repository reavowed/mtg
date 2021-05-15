package mtg.game.state

import mtg.game.PlayerIdentifier

import scala.annotation.tailrec

class GameStateManager(private var currentGameState: GameState) {
  def gameState: GameState = currentGameState

  def initialize(): Unit = {
    executeAutomaticActions()
  }

  @tailrec
  private def executeAutomaticActions(): Unit = currentGameState.nextAction match {
    case automaticGameAction: AutomaticGameAction =>
      currentGameState = automaticGameAction.execute(currentGameState)
      executeAutomaticActions()
    case _ =>
  }

  def handleDecision(serializedDecision: String, actingPlayer: PlayerIdentifier): Unit = currentGameState.nextAction match {
    case choice: Choice =>
      if (choice.playerToAct == actingPlayer) {
        currentGameState = choice.handleDecision(serializedDecision, currentGameState)
        executeAutomaticActions()
      }
    case _ =>
  }
}
