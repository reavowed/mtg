package mtg.game.state

import mtg.game.PlayerId
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
          case HistoryEvent.ResolvedChoice(choice, stateBefore) =>
            if (choice.playerToAct == playerId)
              Some(stateBefore.addUpdates(Seq(choice)))
            else
              None
        }
      } else
        None
    }

    helper(gameState.gameHistory.historyEvents.iterator)
  }

  def canUndo(playerId: PlayerId, gameState: GameState): Boolean = {
    @tailrec
    def helper(iterator: Iterator[HistoryEvent]): Boolean = {
      iterator.hasNext && (iterator.next() match {
        case HistoryEvent.ResolvedAction(action, _) =>
          if (action.canBeReverted) {
            helper(iterator)
          } else {
            false
          }
        case HistoryEvent.ResolvedChoice(choice, _) =>
          choice.playerToAct == playerId
      })
    }

    helper(gameState.gameHistory.historyEvents.iterator)
  }
}
