package mtg.game.state

import mtg.game.{ObjectId, PlayerId, Zone}
import mtg.game.state.history.GameEvent.Decision
import mtg.game.state.history.{GameHistory, LogEvent}

sealed abstract class GameAction

sealed abstract class AutomaticGameAction extends GameAction {
  def execute(currentGameState: GameState): GameActionResult
}

abstract class GameObjectAction extends AutomaticGameAction {
  def execute(currentGameState: GameState): GameObjectActionResult
}

abstract class InternalGameAction extends AutomaticGameAction {
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

