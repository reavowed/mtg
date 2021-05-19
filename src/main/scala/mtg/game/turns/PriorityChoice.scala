package mtg.game.turns

import mtg.game.PlayerIdentifier
import mtg.game.state.history.LogEvent
import mtg.game.state.{ChoiceOption, GameAction, GameState, TypedChoice}

sealed trait PriorityOption extends ChoiceOption
object PriorityOption {
  case object PassPriority extends PriorityOption
}

case class PriorityChoice(playersLeftToAct: Seq[PlayerIdentifier]) extends TypedChoice[PriorityOption] {
  override def playerToAct: PlayerIdentifier = playersLeftToAct.head
  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[PriorityOption] = serializedChosenOption match {
    case "P" => Some(PriorityOption.PassPriority)
    case _ => None
  }
  override def handleDecision(chosenOption: PriorityOption, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    chosenOption match {
      case PriorityOption.PassPriority =>
        playersLeftToAct match {
          case _ +: tail if tail.nonEmpty =>
            (Seq(PriorityChoice(tail)), None)
          case _ =>
            (Nil, None)
        }
    }
  }
}
