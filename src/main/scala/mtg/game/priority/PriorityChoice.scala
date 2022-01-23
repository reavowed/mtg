package mtg.game.priority

import mtg.game.PlayerId
import mtg.game.priority.actions.{ActivateAbilityAction, CastSpellAction, PlayLandAction, PriorityAction}
import mtg.game.state._

case class PriorityChoice(
  playerToAct: PlayerId,
  availableActions: Seq[PriorityAction],
) extends DirectChoice[PriorityDecision] {

  object TakeAction {
    def unapply(string: String): Option[PriorityAction] = {
      if (string.startsWith("Play ")) {
        string.substring("Play ".length).toIntOption.flatMap(id => availableActions.ofType[PlayLandAction].find(_.land.gameObject.objectId.sequentialId == id))
      } else if (string.startsWith("Cast ")) {
        string.substring("Cast ".length).toIntOption.flatMap(id => availableActions.ofType[CastSpellAction].find(_.objectToCast.gameObject.objectId.sequentialId == id))
      } else if (string.startsWith("Activate ")) {
        (string.substring("Activate ".length).split(" ").toSeq match {
          case Seq(aText, bText) => aText.toIntOption.flatMap(a => bText.toIntOption.map(a -> _))
          case _ => None
        }).flatMap { case (objectId, abilityIndex) =>
          availableActions.ofType[ActivateAbilityAction].find(a => a.objectWithAbility.gameObject.objectId.sequentialId == objectId && a.abilityIndex == abilityIndex)
        }
      } else {
        None
      }
    }
  }

  def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[PriorityDecision] = serializedDecision match {
    case "Pass" =>
      Some(PriorityDecision.Pass)
    case TakeAction(action) =>
      Some(PriorityDecision.TakeAction(action, gameState))
    case _ =>
      None
  }
}
object PriorityChoice {
  def apply(playerToAct: PlayerId)(implicit gameState: GameState): PriorityChoice = {
    PriorityChoice(playerToAct, PriorityAction.getAll(playerToAct, gameState))
  }
}

sealed trait PriorityDecision
object PriorityDecision {
  case object Pass extends PriorityDecision
  case class TakeAction(priorityAction: PriorityAction, backupState: GameState) extends PriorityDecision
}
