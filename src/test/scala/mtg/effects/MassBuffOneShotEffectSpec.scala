package mtg.effects

import mtg.TestCards.{VanillaOneOne, VanillaTwoTwo}
import mtg.abilities.builder.EffectBuilder._
import mtg.cards.patterns.{Spell, VanillaCreature}
import mtg.characteristics.types.Type
import mtg.characteristics.types.Type.Creature
import mtg.game.turns.{StartNextTurnAction, TurnPhase}
import mtg.helpers.SpecWithTestCards
import mtg.parts.costs.ManaCost

class MassBuffOneShotEffectSpec extends SpecWithTestCards {
  object TestCard extends Spell(
    "Card",
    ManaCost(0),
    Type.Instant,
    Nil,
    Creature(you.control)(get(1, 1)).until(endOfTurn))

  override def testCards = Seq(TestCard, VanillaOneOne, VanillaTwoTwo)

  "mass buff effect" should {
    "have correct oracle text" in {
      TestCard.text mustEqual "Creatures you control get +1/+1 until end of turn."
    }

    "apply filter correctly" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCard)
        .setBattlefield(playerOne, Seq(VanillaOneOne, VanillaTwoTwo))
        .setBattlefield(playerTwo, VanillaOneOne)

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestCard)
      manager.resolveNext()

      manager.getPermanent(VanillaOneOne, playerOne) must havePowerAndToughness(2, 2)
      manager.getPermanent(VanillaTwoTwo, playerOne) must havePowerAndToughness(3, 3)
      manager.getPermanent(VanillaOneOne, playerTwo) must havePowerAndToughness(1, 1)
    }

    "only apply to initial set of objects" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(TestCard, VanillaOneOne))
        .setBattlefield(playerOne, VanillaTwoTwo)

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestCard)
      manager.resolveNext()
      manager.castSpell(playerOne, VanillaOneOne)
      manager.resolveNext()

      manager.getPermanent(VanillaOneOne, playerOne) must havePowerAndToughness(1, 1)
    }
  }
}
