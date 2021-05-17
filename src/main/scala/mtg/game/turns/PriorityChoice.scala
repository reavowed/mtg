package mtg.game.turns

import mtg.game.PlayerIdentifier
import mtg.game.state.{GameAction, ChoiceOption, GameState, LogEvent, TypedChoice}

trait PriorityOption extends ChoiceOption

case class PriorityChoice(playersLeftToAct: Seq[PlayerIdentifier]) extends TypedChoice[PriorityOption] {
  override def playerToAct: PlayerIdentifier = playersLeftToAct.head
  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[PriorityOption] = ???
  override def handleDecision(chosenOption: PriorityOption, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = ???
}
