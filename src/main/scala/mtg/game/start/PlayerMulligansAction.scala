package mtg.game.start

import mtg.game.{GameData, PlayerIdentifier}
import mtg.game.objects.GameObjectState
import mtg.game.state.{Action, Choice, Transition}

object PlayerMulligansAction extends Action {

  sealed trait MulliganDecision
  object TakeMulligan extends MulliganDecision
  object KeepHand extends MulliganDecision

  case class MulliganChoice(
      decisionsAlreadyMade: Map[PlayerIdentifier, MulliganDecision],
      playerToAct: PlayerIdentifier,
      playersWaitingToAct: Seq[PlayerIdentifier])
    extends Choice
  {
    override def handleDecision(serializedAction: String, currentGameObjectState: GameObjectState, gameData: GameData): Transition = ???
  }

  override def runAction(currentGameObjectState: GameObjectState, gameData: GameData): (GameObjectState, Transition) = {
    (currentGameObjectState, MulliganChoice(Map.empty, gameData.playersInTurnOrder.head, gameData.playersInTurnOrder.tail))
  }
}
