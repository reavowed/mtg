package mtg.game.state

import mtg.game.state.history.GameEvent.Decision
import mtg.game.state.history.{GameEvent, LogEvent}

abstract class TypedPlayerChoice[TOption <: AnyRef] extends PlayerChoice {
  def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[TOption]
  def handleDecision(chosenOption: TOption, currentGameState: GameState): InternalGameActionResult
  override def handleDecision(serializedChosenOption: String, currentGameState: GameState): Option[(Decision, InternalGameActionResult)] = {
    parseOption(serializedChosenOption, currentGameState)
      .map(option => {
        (
          GameEvent.Decision(option, playerToAct),
          handleDecision(option, currentGameState)
        )
      })
  }
}
object TypedPlayerChoice {
  trait PartialFunctionParser[TOption] {
    def optionParser(currentGameState: GameState): PartialFunction[String, TOption]
    def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[TOption] = {
      optionParser(currentGameState).lift(serializedChosenOption)
    }
  }
}
