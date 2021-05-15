package mtg.game.state

import mtg.game.PlayerIdentifier
import mtg.game.state.GameEvent.Decision

sealed abstract class GameAction

abstract class GameObjectEvent extends GameAction {
  def execute(currentGameState: GameState): GameObjectEventResult
}

abstract class GameActionManager extends GameAction {
  def execute(currentGameState: GameState): Seq[GameAction]
}

abstract class GameOption

abstract class Choice extends GameAction {
  def playerToAct: PlayerIdentifier
  def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(Decision, Seq[GameAction])]
}
abstract class TypedChoice[TOption <: GameOption] extends Choice {
  def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[TOption]
  def handleDecision(chosenOption: TOption, currentGameState: GameState): Seq[GameAction]
  override def handleDecision(serializedChosenOption: String, currentGameState: GameState): Option[(Decision, Seq[GameAction])] = {
    parseOption(serializedChosenOption, currentGameState)
      .map(option => (GameEvent.Decision(option, playerToAct), handleDecision(option, currentGameState)))
  }
}
object TypedChoice {
  trait PartialFunctionParser[TOption <: GameOption] {
    def optionParser(currentGameState: GameState): PartialFunction[String, TOption]
    def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[TOption] = {
      optionParser(currentGameState).lift(serializedChosenOption)
    }
  }
}

sealed abstract class GameResult extends GameAction
object GameResult {
  object Tie extends GameResult
}

