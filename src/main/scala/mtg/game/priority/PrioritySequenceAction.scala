package mtg.game.priority

import mtg.core.PlayerId
import mtg.game.priority.actions.PriorityAction
import mtg.game.state._
import mtg.stack.resolving.ResolveStackObject

case object PrioritySequenceAction extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = fromActivePlayer

  private def fromActivePlayer: GameAction[Unit] = (gameState: GameState) => fromPlayer(gameState.activePlayer)
  private def fromPlayer(player: PlayerId): GameAction[Unit] = (gameState: GameState) => forPlayers(gameState.gameData.getPlayersInApNapOrder(player))

  private def forPlayers(playersToAct: Seq[PlayerId]): GameAction[Unit] = { (gameState: GameState) =>
    implicit def gs = gameState
    getPriorityDecisionFromAnyPlayer(playersToAct) flatMap {
      case Some((player, priorityAction)) =>
        priorityAction.andThen(fromPlayer(player))
      case None =>
        resolveTopStackObject
    }
  }

  private def getPriorityDecisionFromAnyPlayer(playersLeftToAct: Seq[PlayerId]): GameAction[Option[(PlayerId, PriorityAction)]] = {  (gameState: GameState) =>
    implicit def gs = gameState
    playersLeftToAct match {
      case playerToAct +: otherPlayers =>
        for {
          _ <- BeforePriorityCheck
          decision <- PriorityChoice(playerToAct)
          result <- decision match {
            case PriorityDecision.TakeAction(action) =>
              ConstantAction(Some((playerToAct, action)))
            case PriorityDecision.Pass =>
              getPriorityDecisionFromAnyPlayer(otherPlayers)
          }
        } yield result
      case Nil =>
        ConstantAction(None)
    }
  }

  private def resolveTopStackObject: GameAction[Unit] = CalculatedGameAction { (gameState: GameState) =>
    gameState.gameObjectState.stack match {
      case _ :+ topObject =>
        ResolveStackObject(topObject).andThen(fromActivePlayer)
      case Nil =>
        ()
    }
  }
}

object BeforePriorityCheck extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    StateBasedActionCheck flatMap {
      case true =>
        delegate
      case false =>
        TriggeredAbilityCheck flatMap {
          case true =>
            delegate
          case false =>
            ()
        }
    }
  }
}
