package mtg.stack.adding

import mtg.core.symbols.ManaSymbol
import mtg.core.{ObjectId, PlayerId}
import mtg.game.objects.ManaObject
import mtg.game.priority.actions.ActivateAbilityAction
import mtg.game.state._
import mtg.parts.costs.ManaCost

case class PayManaCosts(manaCost: ManaCost, player: PlayerId) extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    payManaAutomatically(player, manaCost.symbols, gameState.gameObjectState.manaPools(player))
  }

  private def payManaAutomatically(player: PlayerId, remainingCost: Seq[ManaSymbol], manaToPay: Seq[ManaObject])(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val (costAfterPayment, manaPaid) = ManaCostAutoPayer.payManaAutomatically(remainingCost, manaToPay)
    if (manaPaid.nonEmpty) {
      PartialGameActionResult.ChildWithCallback(
        WrappedOldUpdates(RemoveManaAction(player, manaPaid)),
        payRemainingManaCost(player, costAfterPayment)(_: Unit)(_))
    } else {
      payRemainingManaCost(player, costAfterPayment)(())
    }
  }

  private def payRemainingManaCost(player: PlayerId, remainingCost: Seq[ManaSymbol])(any: Unit)(implicit gameState: GameState): PartialGameActionResult[Unit] = {
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
        payManaAutomatically(player, remainingCost, Seq(manaObject))(gameState)
      case Right(manaAbilityAction) =>
        PartialGameActionResult.ChildWithCallback(
          manaAbilityAction,
          handleNewMana(player, remainingCost, gameState.gameObjectState.manaPools(player))(_: Unit)(_))
    }
  }

  private def handleNewMana(player: PlayerId, remainingCost: Seq[ManaSymbol], manaPreviouslyInPool: Seq[ManaObject])(any: Unit)(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val addedMana = gameState.gameObjectState.manaPools(player).diff(manaPreviouslyInPool)
    payManaAutomatically(player, remainingCost, addedMana)
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
      .orElse(matchManaPayment(serializedDecision).map(Left(_)))
  }
  private def matchManaPayment(serializedDecision: String)(implicit gameState: GameState): Option[ManaObject] = {
    if (serializedDecision.startsWith("Pay "))
      gameState.gameObjectState.manaPools(playerToAct).find(_.id.toString == serializedDecision.substring("Pay ".length))
    else
      None
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
