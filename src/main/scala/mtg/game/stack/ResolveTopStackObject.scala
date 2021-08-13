package mtg.game.stack

import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.game.turns.priority.PriorityFromActivePlayerAction

object ResolveTopStackObject extends InternalGameAction {
  override def execute(currentGameState: GameState): GameActionResult = {
    currentGameState.gameObjectState.stack match {
      case _ :+ topObject =>
        Seq(ResolveStackObject(topObject), PriorityFromActivePlayerAction)
      case Nil =>
        ()
    }
  }
}
