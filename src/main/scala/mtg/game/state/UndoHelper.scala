package mtg.game.state

import mtg.core.PlayerId
import mtg.game.priority.PriorityDecision
import mtg.game.state.history.HistoryEvent

import scala.annotation.tailrec

object UndoHelper {

  def requestUndo(playerId: PlayerId, gameState: GameState): Option[GameState] = {
    @tailrec
    def helper(iterator: Iterator[HistoryEvent]): Option[GameState]  = {
      if (iterator.hasNext) {
        iterator.next() match {
          case HistoryEvent.ResolvedAction(action, _) =>
            if (action.canBeReverted) {
              helper(iterator)
            } else None
          case HistoryEvent.ResolvedChoice(choice, decision, stateBefore) =>
            if (choice.playerToAct == playerId && decision != PriorityDecision.Pass)
              Some(stateBefore)
            else
              None
        }
      } else
        None
    }

    helper(gameState.gameHistory.historyEvents.iterator)
  }

  def canUndo(playerId: PlayerId, gameState: GameState): Boolean = {
    requestUndo(playerId, gameState).isDefined
  }
}
