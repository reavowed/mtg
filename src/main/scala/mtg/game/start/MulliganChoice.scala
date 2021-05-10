package mtg.game.start

import mtg.game.{GameState, PlayerIdentifier}
import mtg.game.loop.{Choice, Action, GameResult}

sealed trait MulliganDecision
object TakeMulligan extends MulliganDecision
object KeepHand extends MulliganDecision

case class MulliganChoice(
    decisionsAlreadyMade: Map[PlayerIdentifier, MulliganDecision],
    playerToAct: PlayerIdentifier,
    playersToDecide: Seq[PlayerIdentifier],
    gameState: GameState)
  extends Choice
{
  override def handleAction(serializedAction: String): Either[Action, Either[Choice, GameResult]] = {
    ???
  }
}

object MulliganChoice {
  def initial(gameState: GameState): MulliganChoice = {
    MulliganChoice(Map.empty, gameState.playersInTurnOrder.head, gameState.playersInTurnOrder.tail, gameState)
  }
}
