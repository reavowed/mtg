package mtg.game.turns

import mtg.game.PlayerIdentifier
import mtg.game.actions.{PlayLandAction, PriorityAction}
import mtg.game.state._
import mtg.game.state.history.LogEvent

sealed trait PriorityOption extends ChoiceOption
object PriorityOption {
  case object PassPriority extends PriorityOption
  case class PlayLand(playLandAction: PlayLandAction) extends PriorityOption
}

case class PriorityChoice(
  playerToAct: PlayerIdentifier,
  remainingPlayers: Seq[PlayerIdentifier],
  availableActions: Seq[PriorityAction],
) extends TypedChoice[PriorityOption] {

  object PlayLand {
    def unapply(string: String): Option[PlayLandAction] = {
      if (string.startsWith("Play ")) {
        string.substring("Play ".length).toIntOption.flatMap(id => availableActions.ofType[PlayLandAction].find(_.land.gameObject.objectId.sequentialId == id))
      } else {
        None
      }
    }
  }

  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[PriorityOption] = serializedChosenOption match {
    case "Pass" =>
      Some(PriorityOption.PassPriority)
    case PlayLand(action) =>
      Some(PriorityOption.PlayLand(action))
    case _ => None
  }
  override def handleDecision(chosenOption: PriorityOption, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    chosenOption match {
      case PriorityOption.PassPriority =>
        if (remainingPlayers.nonEmpty)
          (PriorityChoice.create(remainingPlayers, currentGameState).toSeq, None)
        else
          (Nil, None)
      case PriorityOption.PlayLand(playLandAction) =>
        (Seq(playLandAction, AllPlayersGetPriorityAction), None)
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
          PriorityAction.getAll(playerToAct, gameState)))
      case Nil =>
        None
    }
  }
}
