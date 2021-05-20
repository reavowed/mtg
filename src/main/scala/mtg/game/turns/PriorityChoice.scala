package mtg.game.turns

import mtg.game.actions.PlayLandAction
import mtg.game.state.history.LogEvent
import mtg.game.state._
import mtg.game.{PlayerIdentifier, actions}

sealed trait PriorityOption extends ChoiceOption
object PriorityOption {
  case object PassPriority extends PriorityOption
  case class PlayLand(land: ObjectWithState) extends PriorityOption
}

case class PriorityChoice(playerToAct: PlayerIdentifier, remainingPlayers: Seq[PlayerIdentifier], playableLands: Seq[ObjectWithState]) extends TypedChoice[PriorityOption] {

  object PlayLand {
    def unapply(string: String): Option[ObjectWithState] = {
      if (string.startsWith("Play ")) {
        string.substring("Play ".length).toIntOption.flatMap(id => playableLands.find(_.gameObject.objectId.sequentialId == id))
      } else {
        None
      }
    }
  }

  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[PriorityOption] = serializedChosenOption match {
    case "Pass" =>
      Some(PriorityOption.PassPriority)
    case PlayLand(landCard) =>
      Some(PriorityOption.PlayLand(landCard))
    case _ => None
  }
  override def handleDecision(chosenOption: PriorityOption, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    chosenOption match {
      case PriorityOption.PassPriority =>
        if (remainingPlayers.nonEmpty)
          (PriorityChoice.create(remainingPlayers, currentGameState).toSeq, None)
        else
          (Nil, None)
      case PriorityOption.PlayLand(landCard) =>
        (Seq(actions.PlayLandAction(playerToAct, landCard), PriorityAction), None)
    }
  }
}

object PriorityChoice {
  def create(playersLeftToAct: Seq[PlayerIdentifier], gameState: GameState): Option[PriorityChoice] = {
    playersLeftToAct match {
      case playerToAct +: remainingPlayers =>
        Some(PriorityChoice(
          playerToAct,
          remainingPlayers,
          PlayLandAction.getPlayableLands(playerToAct, gameState)))
      case Nil =>
        None
    }
  }
}
