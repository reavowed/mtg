package mtg.game.state

import mtg.game.PlayerId
import mtg.game.state.history.GameEvent.Decision
import mtg.game.state.history.{GameHistory, LogEvent}

sealed abstract class GameAction

sealed abstract class GameEvent extends GameAction

abstract class GameObjectEvent extends GameEvent {
  def execute(currentGameState: GameState): GameObjectEventResult
}

abstract class TurnCycleEvent extends GameEvent {
  def execute(currentGameState: GameState): (GameHistory => GameHistory, GameActionResult)
}

abstract class InternalGameAction extends GameAction {
  def execute(currentGameState: GameState): GameActionResult
}

case class BackupAction(gameStateToRevertTo: GameState) extends GameAction

abstract class PlayerChoice extends GameAction {
  def playerToAct: PlayerId
  def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(Decision, GameActionResult)]
}

sealed abstract class GameResult extends GameAction
object GameResult {
  object Tie extends GameResult
}

