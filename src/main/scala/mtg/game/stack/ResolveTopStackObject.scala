package mtg.game.stack

import mtg.game.state.{InternalGameActionResult, GameState, InternalGameAction}
import mtg.game.turns.priority.PriorityFromActivePlayerAction

object ResolveTopStackObject extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    currentGameState.gameObjectState.stack match {
      case _ :+ topObject =>
        Seq(ResolveStackObject(topObject), PriorityFromActivePlayerAction)
      case Nil =>
        ()
    }
  }
  override def canBeReverted: Boolean = false
}
