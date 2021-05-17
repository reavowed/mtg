package mtg.game.state

import mtg.game.state.history.GameEvent.Decision
import mtg.game.state.history.{GameEvent, LogEvent}

abstract class ChoiceOption

abstract class TypedChoice[TOption <: ChoiceOption] extends Choice {
  def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[TOption]
  def handleDecision(chosenOption: TOption, currentGameState: GameState): (Seq[GameAction], Option[LogEvent])
  override def handleDecision(serializedChosenOption: String, currentGameState: GameState): Option[(Decision, Seq[GameAction], Option[LogEvent])] = {
    parseOption(serializedChosenOption, currentGameState)
      .map(option => {
        val decision = GameEvent.Decision(option, playerToAct)
        val (actions, logEvent) = handleDecision(option, currentGameState)
        (decision, actions, logEvent)
      })
  }
}
object TypedChoice {
  trait PartialFunctionParser[TOption <: ChoiceOption] {
    def optionParser(currentGameState: GameState): PartialFunction[String, TOption]
    def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[TOption] = {
      optionParser(currentGameState).lift(serializedChosenOption)
    }
  }
}
