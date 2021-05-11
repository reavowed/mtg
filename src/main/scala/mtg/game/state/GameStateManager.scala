package mtg.game.state

import scala.annotation.tailrec

class GameStateManager(private var currentGameState: GameState) {
  def gameState: GameState = currentGameState

  executeWhileAction()

  @tailrec
  private def executeWhileAction(): Unit = currentGameState.nextTransition match {
    case action: Action =>
      val (newObjectState, newTransition) = action.runAction(currentGameState.gameObjectState, currentGameState.gameData)
      currentGameState = currentGameState.copy(gameObjectState = newObjectState, nextTransition = newTransition)
      executeWhileAction()
    case _ =>
  }
}
