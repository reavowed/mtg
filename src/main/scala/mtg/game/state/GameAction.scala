package mtg.game.state

import mtg.game.{ObjectId, PlayerId, Zone}
import mtg.game.state.history.GameEvent.Decision
import mtg.game.state.history.{GameHistory, LogEvent}

sealed trait GameAction

sealed trait AutomaticGameAction extends GameAction

trait GameObjectEvent extends AutomaticGameAction {
  def execute(currentGameState: GameState): GameObjectEventResult
}

trait TurnCycleEvent extends AutomaticGameAction {
  def execute(currentGameState: GameState): (GameHistory => GameHistory, InternalGameActionResult)
}

trait InternalGameAction extends AutomaticGameAction {
  def execute(currentGameState: GameState): InternalGameActionResult
}

case class BackupAction(gameStateToRevertTo: GameState) extends GameAction

abstract class PlayerChoice extends GameAction {
  def playerToAct: PlayerId
  def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(Decision, InternalGameActionResult)]
  def temporarilyVisibleZones: Seq[Zone] = Nil
  def temporarilyVisibleObjects: Seq[ObjectId] = Nil
}

sealed abstract class GameResult extends GameAction
object GameResult {
  object Tie extends GameResult
}

