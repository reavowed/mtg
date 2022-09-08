package mtg.stack.adding

import mtg.actions.RemoveManaAction
import mtg.definitions.symbols.ManaSymbol
import mtg.definitions.{ObjectId, PlayerId}
import mtg.game.objects.ManaObject
import mtg.game.priority.actions.ActivateAbilityAction
import mtg.game.state._
import mtg.parts.costs.ManaCost

case class PayManaCosts(manaCost: ManaCost, player: PlayerId) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    payManaAutomatically(player, manaCost.symbols, gameState.gameObjectState.manaPools(player))
  }

  private def payManaAutomatically(player: PlayerId, remainingCost: Seq[ManaSymbol], manaToPay: Seq[ManaObject])(implicit gameState: GameState): GameAction[Unit] = {
    val (costAfterPayment, manaPaid) = ManaCostAutoPayer.payManaAutomatically(remainingCost, manaToPay)
    if (manaPaid.nonEmpty) {
      RemoveManaAction(player, manaPaid)
        .andThen(payRemainingManaCost(player, costAfterPayment)(_: GameState))
    } else {
      payRemainingManaCost(player, costAfterPayment)
    }
  }

  private def payRemainingManaCost(player: PlayerId, remainingCost: Seq[ManaSymbol])(implicit gameState: GameState): GameAction[Unit] = {
    if (remainingCost.nonEmpty) {
      PayManaChoice(player, ManaCost(remainingCost: _*)) flatMap {
        case Left(manaObject) =>
          payManaAutomatically(player, remainingCost, Seq(manaObject))(_: GameState)
        case Right(manaAbilityAction) =>
          manaAbilityAction
            .andThen(calculateManaChange(gameState).flatMap(payManaAutomatically(player, remainingCost, _)))
      }
    } else {
      ()
    }
  }

  private def calculateManaChange(oldGameState: GameState): GameAction[Seq[ManaObject]] = { (newGameState: GameState) =>
    newGameState.gameObjectState.manaPools(player).diff(oldGameState.gameObjectState.manaPools(player))
  }
}

object PayManaCosts {
  case class ForSpell(spellId: ObjectId) extends DelegatingGameAction[Unit] {
    override def delegate(implicit gameState: GameState): GameAction[Unit] = {
      val spellWithState = gameState.gameObjectState.derivedState.stackObjectStates(spellId)
      val manaCost = spellWithState.characteristics.manaCost.get
      val player = spellWithState.controller
      PayManaCosts(manaCost, player)
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
