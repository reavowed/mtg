package mtg.game.turns

import mtg.game.PlayerIdentifier
import mtg.game.actions.{ActivateAbilityAction, PlayLandAction, PriorityAction}
import mtg.game.state._
import mtg.game.state.history.LogEvent

sealed trait PriorityOption extends ChoiceOption
object PriorityOption {
  case object PassPriority extends PriorityOption
  case class TakeAction(action: PriorityAction) extends PriorityOption
}

case class PriorityChoice(
  playerToAct: PlayerIdentifier,
  remainingPlayers: Seq[PlayerIdentifier],
  availableActions: Seq[PriorityAction],
) extends TypedChoice[PriorityOption] {

  object TakeAction {
    def unapply(string: String): Option[PriorityAction] = {
      if (string.startsWith("Play ")) {
        string.substring("Play ".length).toIntOption.flatMap(id => availableActions.ofType[PlayLandAction].find(_.land.gameObject.objectId.sequentialId == id))
      } else if (string.startsWith("Activate ")) {
        (string.substring("Activate ".length).split(" ").toSeq match {
          case Seq(aText, bText) => aText.toIntOption.flatMap(a => bText.toIntOption.map(a -> _))
          case _ => None
        }).flatMap { case (objectId, abilityIndex) =>
          availableActions.ofType[ActivateAbilityAction].find(a => a.source.gameObject.objectId.sequentialId == objectId && a.abilityIndex == abilityIndex)
        }
      } else {
        None
      }
    }
  }

  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[PriorityOption] = serializedChosenOption match {
    case "Pass" =>
      Some(PriorityOption.PassPriority)
    case TakeAction(action) =>
      Some(PriorityOption.TakeAction(action))
    case _ => None
  }
  override def handleDecision(chosenOption: PriorityOption, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    chosenOption match {
      case PriorityOption.PassPriority =>
        if (remainingPlayers.nonEmpty)
          (PriorityChoice.create(remainingPlayers, currentGameState).toSeq, None)
        else
          (Nil, None)
      case PriorityOption.TakeAction(action) =>
        (Seq(action, PriorityFromPlayerAction(playerToAct)), None)
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
