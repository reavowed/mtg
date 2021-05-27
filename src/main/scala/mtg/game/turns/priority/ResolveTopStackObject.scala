package mtg.game.turns.priority

import mtg.game.state.{GameState, InternalGameAction, InternalGameActionResult}

object ResolveTopStackObject extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    currentGameState.gameObjectState.stack match {
      case _ :+ topObject =>
        ResolveStackObject(topObject)
      case Nil =>
        ()
    }
  }
}
