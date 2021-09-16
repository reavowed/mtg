package mtg.game.turns.priority

import mtg.game.PlayerId
import mtg.game.actions.cast.CastSpellAction
import mtg.game.actions.{ActivateAbilityAction, PlayLandAction, PriorityAction}
import mtg.game.state._
import mtg.stack.resolving.ResolveTopStackObject

case class PriorityChoice(
  playerToAct: PlayerId,
  remainingPlayers: Seq[PlayerId],
  availableActions: Seq[PriorityAction],
) extends Choice {

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

  override def parseDecision(serializedChosenOption: String): Option[Decision] = serializedChosenOption match {
    case "Pass" =>
      PassPriority(playerToAct, remainingPlayers)
    case TakeAction(action) =>
      Some(Seq(action, PriorityFromPlayerAction(playerToAct)))
    case _ => None
  }
}

case class PassPriority(playerId: PlayerId, remainingPlayers: Seq[PlayerId]) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
      if (remainingPlayers.nonEmpty)
        PriorityForPlayersAction(remainingPlayers)
      else
        ResolveTopStackObject
  }
  override def canBeReverted: Boolean = false
}
