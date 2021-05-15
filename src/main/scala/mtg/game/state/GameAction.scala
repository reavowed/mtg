package mtg.game.state

import mtg.game.PlayerIdentifier

sealed abstract class GameAction

abstract class GameObjectEvent extends GameAction {
  def execute(currentGameState: GameState): GameObjectEventResult
}

abstract class GameActionManager extends GameAction {
  def execute(currentGameState: GameState): Seq[GameAction]
}

abstract class OptionParser[T] {
  def parseOption: PartialFunction[String, T]
}

abstract class Option

abstract class Choice extends GameAction {
  def playerToAct: PlayerIdentifier
  def handleDecision(serializedDecision: String, currentGameState: GameState): (GameState, Seq[GameAction])
}
abstract class TypedChoice[TOption <: Option] extends Choice {
  def parseOption: PartialFunction[String, TOption]
  def handleDecision(chosenOption: TOption, currentGameState: GameState): (GameState, Seq[GameAction])
  override def handleDecision(serializedChosenOption: String, currentGameState: GameState): (GameState, Seq[GameAction]) = {
    parseOption.lift(serializedChosenOption)
      .map(option => handleDecision(option, currentGameState).mapLeft(_.recordEvent(GameEvent.Decision(option, playerToAct))))
      .getOrElse((currentGameState, Seq(this)))
  }
}

sealed abstract class GameResult extends GameAction
object GameResult {
  object Tie extends GameResult
}

