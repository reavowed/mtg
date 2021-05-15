package mtg.game.state

import mtg.game.PlayerIdentifier

sealed abstract class GameAction

abstract class AutomaticGameAction extends GameAction {
  def execute(currentGameState: GameState): (GameState, GameAction)
}

abstract class OptionParser[T] {
  def parseOption: PartialFunction[String, T]
}

abstract class Option

abstract class Choice extends GameAction {
  def playerToAct: PlayerIdentifier
  def handleDecision(serializedDecision: String, currentGameState: GameState): (GameState, GameAction)
}
abstract class TypedChoice[TOption <: Option] extends Choice {
  def parseOption: PartialFunction[String, TOption]
  def handleDecision(chosenOption: TOption, currentGameState: GameState): (GameState, GameAction)
  override def handleDecision(serializedChosenOption: String, currentGameState: GameState): (GameState, GameAction) = {
    parseOption.lift(serializedChosenOption)
      .map(option => handleDecision(option, currentGameState).mapLeft(_.recordEvents(Seq(GameEvent.Decision(option, playerToAct)))))
      .getOrElse((currentGameState, this))
  }
}

sealed abstract class GameResult extends GameAction
object GameResult {
  object Tie extends GameResult
}

