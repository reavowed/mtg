package mtg.stack.adding

import mtg.core.symbols.ManaSymbol
import mtg.core.{ObjectId, PlayerId}
import mtg.game.objects.ManaObject
import mtg.game.priority.actions.ActivateAbilityAction
import mtg.game.state._
import mtg.parts.costs.ManaCost

import scala.annotation.tailrec

case class ManaPaymentDetails(manaPaid: Seq[ManaObject], manaInPool: Seq[ManaObject]) {
  def payWhere(f: ManaObject => Boolean): Option[ManaPaymentDetails] = {
    manaInPool.findIndex(f).map(index => ManaPaymentDetails(manaPaid :+ manaInPool(index), manaInPool.removeAtIndex(index)))
  }
  def payAll(): ManaPaymentDetails = ManaPaymentDetails(manaPaid ++ manaInPool, Nil)
}


object ManaPaymentDetails {
  def apply(gameState: GameState, playerId: PlayerId): ManaPaymentDetails = ManaPaymentDetails(Nil, gameState.gameObjectState.manaPools(playerId))
}

case class PayManaCosts(manaCost: ManaCost, player: PlayerId) extends ExecutableGameAction[Unit] {
  private def autoPayColoredCosts(symbols: Seq[ManaSymbol], manaPaymentDetails: ManaPaymentDetails): (Seq[ManaSymbol], ManaPaymentDetails) = {
    @tailrec
    def helper(uncheckedSymbols: Seq[ManaSymbol], unpayableSymbols: Seq[ManaSymbol], manaPaymentDetails: ManaPaymentDetails): (Seq[ManaSymbol], ManaPaymentDetails) = {
      uncheckedSymbols match {
        case (symbol: ManaSymbol.ForType) +: remainingSymbols =>
          manaPaymentDetails.payWhere(_.manaType == symbol.manaType) match {
            case Some(newManaPaymentDetails) =>
              helper(remainingSymbols, unpayableSymbols, newManaPaymentDetails)
            case None =>
              helper(remainingSymbols, unpayableSymbols :+ symbol, manaPaymentDetails)
          }
        case otherSymbol +: remainingSymbols =>
            helper(remainingSymbols, unpayableSymbols :+ otherSymbol, manaPaymentDetails)
        case Nil =>
          (unpayableSymbols, manaPaymentDetails)
      }
    }
    helper(symbols, Nil, manaPaymentDetails)
  }

  private def autoPayGenericCosts(manaSymbols: Seq[ManaSymbol], manaPaymentDetails: ManaPaymentDetails): (Seq[ManaSymbol], ManaPaymentDetails) = {
    if (manaSymbols.map(_.asOptionalInstanceOf[ManaSymbol.Generic]).swap.exists(_.map(_.amount).sum == manaPaymentDetails.manaInPool.length)) {
      (Nil, manaPaymentDetails.payAll())
    } else {
      (manaSymbols, manaPaymentDetails)
    }
  }

  private def payManaAutomatically(manaSymbols: Seq[ManaSymbol], manaPaymentDetails: ManaPaymentDetails): (Seq[ManaSymbol], ManaPaymentDetails) = {
    val (symbolsAfterColoredCosts, manaAfterColoredCosts) = autoPayColoredCosts(manaSymbols, manaPaymentDetails)
    val (symbolsAfterGenericCosts, manaAfterGenericCosts) = autoPayGenericCosts(symbolsAfterColoredCosts, manaAfterColoredCosts)
    (symbolsAfterGenericCosts, manaAfterGenericCosts)
  }

  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val initialManaInPool = gameState.gameObjectState.manaPools(player)
    val (remainingCost, remainingManaInPool) = payManaAutomatically(manaCost.symbols, ManaPaymentDetails(gameState, player))
    PartialGameActionResult.ChildWithCallback(
      WrappedOldUpdates(RemoveManaAction(player, remainingManaInPool.manaPaid)),
      payRemainingMana(player, remainingCost)(_: Unit)(_))
  }

  private def payRemainingMana(player: PlayerId, remainingCost: Seq[ManaSymbol])(any: Unit)(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    if (remainingCost.nonEmpty) {
      PartialGameActionResult.ChildWithCallback(
        PayManaChoice(player, ManaCost(remainingCost: _*)),
        handleDecision(player, remainingCost))
    } else {
      PartialGameActionResult.Value(())
    }
  }

  private def handleDecision(player: PlayerId, remainingCost: Seq[ManaSymbol])(decision: Either[ManaObject, ActivateAbilityAction], gameState: GameState): PartialGameActionResult[Unit] = {
    decision match {
      case Left(manaObject) =>
        def costAfter = remainingCost.findIndex(_.asOptionalInstanceOf[ManaSymbol.ForType].exists(_.manaType == manaObject.manaType)).map(i => remainingCost.removeAtIndex(i))
          .orElse(remainingCost.findWithIndexByType[ManaSymbol.Generic].map { case (symbol, index) =>
            if (symbol.amount > 1) {
              remainingCost.updated(index, ManaSymbol.Generic(symbol.amount - 1))
            } else {
              remainingCost.removeAtIndex(index)
            }
          })
        costAfter match {
          case Some(remainingCost) =>
            PartialGameActionResult.ChildWithCallback(
              WrappedOldUpdates(RemoveManaAction(player, Seq(manaObject))),
              payRemainingMana(player, remainingCost)(_: Unit)(_))
          case None =>
            payRemainingMana(player, remainingCost)(())(gameState)
        }
      case Right(manaAbilityAction) =>
        PartialGameActionResult.ChildWithCallback(
          manaAbilityAction,
          payRemainingMana(player, remainingCost)(_: Unit)(_))
    }
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

case class PayManaChoice(playerToAct: PlayerId, remainingCost: ManaCost, availableManaAbilities: Seq[ActivateAbilityAction]) extends Choice[Either[ManaObject, ActivateAbilityAction]] {
  override def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[Either[ManaObject, ActivateAbilityAction]] = {
    ActivateAbilityAction.matchDecision(serializedDecision, availableManaAbilities).map(Right(_))
      .orElse(gameState.gameObjectState.manaPools(playerToAct).find(_.manaType.letter == serializedDecision).map(Left(_)))
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
