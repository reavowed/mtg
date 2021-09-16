package mtg.stack.resolving

import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.game.turns.priority.PriorityFromActivePlayerAction

object ResolveTopStackObject extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.stack match {
      case _ :+ topObject =>
        Seq(ResolveStackObject(topObject), PriorityFromActivePlayerAction)
      case Nil =>
        ()
    }
  }

  override def canBeReverted: Boolean = false
}
