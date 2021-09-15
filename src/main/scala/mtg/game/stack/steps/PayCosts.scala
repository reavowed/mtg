package mtg.game.stack.steps

import mtg.game.ObjectId
import mtg.game.actions.SpendManaAutomaticallyEvent
import mtg.game.objects.ManaObject
import mtg.game.state.{BackupAction, GameState, InternalGameAction, GameActionResult}
import mtg.parts.costs.{GenericManaSymbol, ManaSymbol, ManaTypeSymbol}

import scala.annotation.tailrec

case class PayCosts(stackObjectId: ObjectId, backupAction: BackupAction) extends InternalGameAction {
  @tailrec
  private def payDirectManaCosts(manaTypeSymbols: Seq[ManaTypeSymbol], manaInPool: Seq[ManaObject]): Option[Seq[ManaObject]] = {
    manaTypeSymbols match {
      case symbol +: remainingSymbols =>
        manaInPool.zipWithIndex.findIndex(_._1.manaType == symbol.manaType).map(manaInPool.removeAtIndex) match {
          case Some(remainingManaInPool) =>
            payDirectManaCosts(remainingSymbols, remainingManaInPool)
          case None =>
            None
        }
      case Nil =>
        Some(manaInPool)
    }
  }

  private def payGenericManaCosts(genericManaSymbols: Seq[GenericManaSymbol], manaInPool: Seq[ManaObject]): Option[Seq[ManaObject]] = {
    if (genericManaSymbols.map(_.amount).sum == manaInPool.length) {
      Some(Nil)
    } else {
      None
    }
  }

  private def payManaAutomatically(manaSymbols: Seq[ManaSymbol], manaInPool: Seq[ManaObject]): Option[Seq[ManaObject]] = {
    val (manaTypeSymbols, nonManaTypeSymbols) = manaSymbols.splitByType[ManaTypeSymbol]
    for {
      manaAfterSymbols <- payDirectManaCosts(manaTypeSymbols, manaInPool)
      (genericManaSymbols, otherSymbols) = nonManaTypeSymbols.splitByType[GenericManaSymbol]
      manaAfterGeneric <- payGenericManaCosts(genericManaSymbols, manaAfterSymbols)
      if otherSymbols.isEmpty
    } yield manaAfterGeneric
  }

  override def execute(gameState: GameState): GameActionResult = {
    gameState.gameObjectState.derivedState.spellStates.get(stackObjectId).toSeq.map { stackObjectWithState =>
      stackObjectWithState.characteristics.manaCost match {
        case Some(cost) =>
          val initialManaInPool = gameState.gameObjectState.manaPools(stackObjectWithState.controller)
          val finalManaInPool = payManaAutomatically(cost.symbols, initialManaInPool)
          finalManaInPool match {
            case Some(finalManaInPool) =>
              SpendManaAutomaticallyEvent(stackObjectWithState.controller, finalManaInPool)
            case None =>
              backupAction
          }
        case None =>
          backupAction
      }
    }
  }
  override def canBeReverted: Boolean = true
}
