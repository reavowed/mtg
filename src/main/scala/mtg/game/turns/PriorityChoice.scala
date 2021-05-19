package mtg.game.turns

import mtg.characteristics.types.Type
import mtg.events.PlayLandEvent
import mtg.game.PlayerIdentifier
import mtg.game.objects.CardObject
import mtg.game.state.history.LogEvent
import mtg.game.state.{ChoiceOption, GameAction, GameState, TypedChoice}

sealed trait PriorityOption extends ChoiceOption
object PriorityOption {
  case object PassPriority extends PriorityOption
  case class PlayLand(landCard: CardObject) extends PriorityOption
}

case class PriorityChoice(playerToAct: PlayerIdentifier, remainingPlayers: Seq[PlayerIdentifier], playableLands: Seq[CardObject]) extends TypedChoice[PriorityOption] {

  object PlayLand {
    def unapply(string: String): Option[CardObject] = {
      if (string.startsWith("Play ")) {
        string.substring("Play ".length).toIntOption.flatMap(id => playableLands.find(_.objectId.sequentialId == id))
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
        (Seq(PlayLandEvent(landCard), PriorityAction), None)
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
          gameState.gameObjectState.hands(playerToAct).ofType[CardObject].filter(_.card.printing.cardDefinition.types.contains(Type.Land))))
      case Nil =>
        None
    }
  }
}
