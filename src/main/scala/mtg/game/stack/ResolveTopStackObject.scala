package mtg.game.stack

import mtg.game.state.{GameActionResult, GameState, InternalGameAction}

object ResolveTopStackObject extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    currentGameState.gameObjectState.stack match {
      case _ :+ topObject =>
        ResolveStackObject(topObject)
      case Nil =>
        ()
    }
  }
}
