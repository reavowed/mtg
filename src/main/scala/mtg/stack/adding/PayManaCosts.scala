package mtg.stack.adding

import mtg.abilities.ManaAbility
import mtg.core.{ObjectId, PlayerId}
import mtg.core.symbols.ManaSymbol
import mtg.game.objects.ManaObject
import mtg.game.priority.actions.ActivateAbilityAction
import mtg.game.state._
import mtg.parts.costs.ManaCost

import scala.annotation.tailrec

case class PayManaCosts(manaCost: ManaCost, player: PlayerId) extends ExecutableGameAction[Unit] {
  private def autoPayColoredCosts(symbols: Seq[ManaSymbol], manaInPool: Seq[ManaObject]): (Seq[ManaSymbol], Seq[ManaObject]) = {
    @tailrec
    def helper(uncheckedSymbols: Seq[ManaSymbol], unpayableSymbols: Seq[ManaSymbol], manaInPool: Seq[ManaObject]): (Seq[ManaSymbol], Seq[ManaObject]) = {
      uncheckedSymbols match {
        case (symbol: ManaSymbol.ForType) +: remainingSymbols =>
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
    if (manaSymbols.map(_.asOptionalInstanceOf[ManaSymbol.Generic]).swap.exists(_.map(_.amount).sum == manaInPool.length)) {
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
    val initialManaInPool = gameState.gameObjectState.manaPools(player)
    val (remainingCost, remainingManaInPool) = payManaAutomatically(manaCost.symbols, initialManaInPool)
    PartialGameActionResult.ChildWithCallback(
      WrappedOldUpdates(SpendManaAutomaticallyEvent(player, remainingManaInPool)),
      payRemainingMana(player, remainingCost)(_: Unit)(_))
  }

  private def payRemainingMana(player: PlayerId, remainingCost: Seq[ManaSymbol])(any: Unit)(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    if (remainingCost.nonEmpty) {
      PartialGameActionResult.ChildWithCallback(
        PayManaChoice(player, ManaCost(remainingCost: _*)),
        handleManaAbility(player, remainingCost))
    } else {
      PartialGameActionResult.Value(())
    }
  }

  private def handleManaAbility(player: PlayerId, remainingCost: Seq[ManaSymbol])(manaAbilityAction: ActivateAbilityAction, gameState: GameState): PartialGameActionResult[Unit] = {
    PartialGameActionResult.ChildWithCallback(
      manaAbilityAction,
      payRemainingMana(player, remainingCost)(_: Unit)(_))
  }
}

object PayManaCosts {
  case class ForSpell(spellId: ObjectId) extends ExecutableGameAction[Unit] {
    override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
      val spellWithState = gameState.gameObjectState.derivedState.stackObjectStates(spellId)
      val manaCost = spellWithState.characteristics.manaCost.get
      val player = spellWithState.controller
      PartialGameActionResult.child(PayManaCosts(manaCost, player))
    }
  }
}

case class PayManaChoice(playerToAct: PlayerId, remainingCost: ManaCost, availableManaAbilities: Seq[ActivateAbilityAction]) extends Choice[ActivateAbilityAction] {
  override def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[ActivateAbilityAction] = {
    ActivateAbilityAction.matchDecision(serializedDecision, availableManaAbilities)
  }
}
object PayManaChoice {
  def apply(playerToAct: PlayerId, remainingCost: ManaCost)(implicit gameState: GameState): PayManaChoice = {
    PayManaChoice(
      playerToAct,
      remainingCost,
      ActivateAbilityAction.getActivatableAbilities(playerToAct, gameState)
        .filter(_.ability.isManaAbility))
  }
}
