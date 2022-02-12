package mtg.stack.adding

import mtg.game.objects.ManaObject
import mtg.game.state._
import mtg.game.{ObjectId, PlayerId}
import mtg.parts.costs.{GenericManaSymbol, ManaCost, ManaSymbol, ManaTypeSymbol}

import scala.annotation.tailrec

case class PayManaCosts(stackObjectId: ObjectId) extends ExecutableGameAction[Unit] {
  private def autoPayColoredCosts(symbols: Seq[ManaSymbol], manaInPool: Seq[ManaObject]): (Seq[ManaSymbol], Seq[ManaObject]) = {
    @tailrec
    def helper(uncheckedSymbols: Seq[ManaSymbol], unpayableSymbols: Seq[ManaSymbol], manaInPool: Seq[ManaObject]): (Seq[ManaSymbol], Seq[ManaObject]) = {
      uncheckedSymbols match {
        case (symbol: ManaTypeSymbol) +: remainingSymbols =>
          manaInPool.findIndex(_.manaType == symbol.manaType) match {
            case Some(index) =>
              helper(remainingSymbols, unpayableSymbols, manaInPool.removeAtIndex(index))
            case None =>
              helper(remainingSymbols, unpayableSymbols :+ symbol, manaInPool)
          }
        case otherSymbol +: remainingSymbols =>
            helper(remainingSymbols, unpayableSymbols :+ otherSymbol, manaInPool)
        case Nil =>
          (unpayableSymbols, manaInPool)
      }
    }
    helper(symbols, Nil, manaInPool)
  }

  private def autoPayGenericCosts(manaSymbols: Seq[ManaSymbol], manaInPool: Seq[ManaObject]): (Seq[ManaSymbol], Seq[ManaObject]) = {
    if (manaSymbols.map(_.asOptionalInstanceOf[GenericManaSymbol]).swap.exists(_.map(_.amount).sum == manaInPool.length)) {
      (Nil, Nil)
    } else {
      (manaSymbols, manaInPool)
    }
  }

  private def payManaAutomatically(manaSymbols: Seq[ManaSymbol], manaInPool: Seq[ManaObject]): (Seq[ManaSymbol], Seq[ManaObject]) = {
    val (symbolsAfterColoredCosts, manaAfterColoredCosts) = autoPayColoredCosts(manaSymbols, manaInPool)
    val (symbolsAfterGenericCosts, manaAfterGenericCosts) = autoPayGenericCosts(symbolsAfterColoredCosts, manaAfterColoredCosts)
    (symbolsAfterGenericCosts, manaAfterGenericCosts)
  }

  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObjectId)
    val player = stackObjectWithState.controller
    val manaCost = stackObjectWithState.characteristics.manaCost.get
    val initialManaInPool = gameState.gameObjectState.manaPools(player)
    val (remainingCost, remainingManaInPool) = payManaAutomatically(manaCost.symbols, initialManaInPool)
    PartialGameActionResult.ChildWithCallback(
      WrappedOldUpdates(SpendManaAutomaticallyEvent(stackObjectWithState.controller, remainingManaInPool)),
      payRemainingMana(player, remainingCost))
  }

  private def payRemainingMana(player: PlayerId, remainingCost: Seq[ManaSymbol])(any: Any, gameState: GameState): PartialGameActionResult[Unit] = {
    if (remainingCost.nonEmpty) {
      PartialGameActionResult.childThenValue(PayManaChoice(player, ManaCost(remainingCost: _*)), ())(gameState)
    } else {
      PartialGameActionResult.Value(())
    }
  }
}

case class PayManaChoice(playerToAct: PlayerId, remainingCost: ManaCost) extends DirectChoice[(ManaSymbol, ManaObject)] {
  override def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[(ManaSymbol, ManaObject)] = {
    None
  }
}
