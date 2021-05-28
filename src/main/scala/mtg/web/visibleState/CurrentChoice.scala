package mtg.web.visibleState

import mtg.game.state.PlayerChoice

case class CurrentChoice(`type`: String, playerToAct: String, details: Any)
object CurrentChoice {
  def apply(choice: PlayerChoice): CurrentChoice = CurrentChoice(choice.getClass.getSimpleName, choice.playerToAct.id, choice)
}
