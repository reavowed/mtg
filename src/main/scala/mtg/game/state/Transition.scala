package mtg.game.state

import mtg.game.PlayerIdentifier

sealed abstract class Transition

abstract class AutomaticGameAction extends Transition {
  def execute(currentGameState: GameState): GameState
}

abstract class OptionParser[T] {
  def parseOption: PartialFunction[String, T]
}

abstract class Option

abstract class Choice extends Transition {
  def playerToAct: PlayerIdentifier
  def handleDecision(serializedDecision: String, currentGameState: GameState): GameState
}
abstract class TypedChoice[TOption <: Option] extends Choice {
  def parseOption: PartialFunction[String, TOption]
  def handleDecision(chosenOption: TOption, currentGameState: GameState): GameState
  override def handleDecision(serializedChosenOption: String, currentGameState: GameState): GameState = {
    parseOption.lift(serializedChosenOption)
      .map(option => handleDecision(option, currentGameState).recordEvents(Seq(GameEvent.Decision(option, playerToAct))))
      .getOrElse(currentGameState)
  }
}

sealed abstract class GameResult extends Transition
object GameResult {
  object Tie extends GameResult
}

