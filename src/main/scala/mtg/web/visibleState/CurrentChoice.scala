package mtg.web.visibleState

import mtg.game.state.Choice

case class CurrentChoice(`type`: String, playerToAct: String, details: Any)
object CurrentChoice {
  def apply(choice: Choice): CurrentChoice = CurrentChoice(choice.getClass.getSimpleName, choice.playerToAct.id, choice)
}
