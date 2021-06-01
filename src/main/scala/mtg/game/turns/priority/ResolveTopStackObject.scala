package mtg.game.turns.priority

import mtg.game.state.{GameState, InternalGameAction, GameActionResult}

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
