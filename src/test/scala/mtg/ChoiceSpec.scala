package mtg

import mtg.game.state.GameAction
import mtg.game.state.history.GameEvent.Decision

abstract class ChoiceSpec extends SpecWithGameObjectState {
  def checkResultAndGetActions(choiceResult: Option[(Decision, Seq[GameAction])]): Seq[GameAction] = {
    choiceResult must beSome
    choiceResult.get._2
  }
}
