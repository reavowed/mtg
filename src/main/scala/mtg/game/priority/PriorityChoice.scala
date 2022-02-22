package mtg.game.priority

import mtg.core.PlayerId
import mtg.game.priority.actions.{ActivateAbilityAction, CastSpellAction, PlayLandAction, PriorityAction}
import mtg.game.state._

case class PriorityChoice(
  playerToAct: PlayerId,
  availableActions: Seq[PriorityAction],
) extends Choice[PriorityDecision] {

  object TakeAction {
    def unapply(string: String): Option[PriorityAction] = {
      if (string.startsWith("Play ")) {
        availableActions.ofType[PlayLandAction].find(_.land.gameObject.objectId.toString == string.substring("Play ".length))
      } else if (string.startsWith("Cast ")) {
        availableActions.ofType[CastSpellAction].find(_.objectToCast.gameObject.objectId.toString == string.substring("Cast ".length))
      } else {
        ActivateAbilityAction.matchDecision(string, availableActions.ofType[ActivateAbilityAction])
      }
    }
  }

  def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[PriorityDecision] = serializedDecision match {
    case "Pass" =>
      Some(PriorityDecision.Pass)
    case TakeAction(action) =>
      Some(PriorityDecision.TakeAction(action))
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
  case class TakeAction(priorityAction: PriorityAction) extends PriorityDecision
}
