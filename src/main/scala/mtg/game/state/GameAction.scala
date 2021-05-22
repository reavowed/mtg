package mtg.game.state

import mtg.game.PlayerIdentifier
import mtg.game.state.history.GameEvent.Decision
import mtg.game.state.history.{GameHistory, LogEvent}

sealed abstract class GameAction

abstract class GameObjectEvent extends GameAction {
  def execute(currentGameState: GameState): GameObjectEventResult
}

abstract class TurnCycleEvent extends GameAction {
  def execute(currentGameState: GameState): (GameHistory => GameHistory, Seq[GameAction], Option[LogEvent])
}

abstract class InternalGameAction extends GameAction {
  def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent])
}

case class BackupAction(gameStateToRevertTo: GameState) extends GameAction

abstract class Choice extends GameAction {
  def playerToAct: PlayerIdentifier
  def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(Decision, Seq[GameAction], Option[LogEvent])]
}

sealed abstract class GameResult extends GameAction
object GameResult {
  object Tie extends GameResult
}

