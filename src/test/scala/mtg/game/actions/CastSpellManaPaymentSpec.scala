package mtg.game.actions

import mtg.cards.patterns.{Creature, Spell}
import mtg.characteristics.Color
import mtg.characteristics.Color.White
import mtg.data.cards.Plains
import mtg.data.cards.strixhaven.AgelessGuardian
import mtg.game.priority.PriorityChoice
import mtg.game.priority.actions.{ActivateAbilityAction, CastSpellAction}
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.helpers.SpecWithTestCards
import mtg.parts.costs.ManaCost
import mtg.stack.adding.PayManaChoice

class CastSpellManaPaymentSpec extends SpecWithTestCards {
  val Creature = new Creature("Creature", ManaCost(White), Nil, Nil, (1, 1))
  override def testCards = Seq(Creature)

  "casting a creature that costs {W}" should {
    "offer payment choice with no mana in pool" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(Creature))
        .setBattlefield(playerOne, Seq(Plains, Plains))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      manager.castSpell(playerOne, Creature)

      manager.currentChoice must beSome(beAnInstanceOf[PayManaChoice])
    }

    "move the card to the stack with correct mana in pool" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(Creature))
        .setBattlefield(playerOne, Seq(Plains))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      // Add necessary mana
      manager.activateAbility(playerOne, Plains)
      manager.gameState.gameObjectState.manaPools(playerOne).map(_.manaType) must contain(exactly(Color.White.manaType))

      // Cast spell
      manager.castSpell(playerOne, Creature)

      manager.gameState.gameObjectState.manaPools(playerOne) must beEmpty
      manager.gameState.gameObjectState.hands(playerOne) must beEmpty
      manager.gameState.gameObjectState.stack must contain(exactly(beCardObject(Creature)))
    }
  }
}
