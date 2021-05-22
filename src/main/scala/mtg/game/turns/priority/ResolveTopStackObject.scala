package mtg.game.turns.priority

import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}

object ResolveTopStackObject extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    currentGameState.gameObjectState.stack match {
      case _ :+ topObject =>
        (Seq(ResolveStackObject(topObject)), None)
      case Nil =>
        (Nil, None)
    }
  }
}
