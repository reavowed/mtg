package mtg.filters

import mtg.TestCards.vanillaCreature
import mtg.abilities.builder.EffectBuilder._
import mtg.cards.patterns.Spell
import mtg.core.types.Type
import mtg.core.types.Type.Creature
import mtg.game.turns.TurnPhase
import mtg.helpers.SpecWithTestCards
import mtg.parts.costs.ManaCost

class PowerConstantOrGreaterFilterSpec extends SpecWithTestCards {
  object TestCard extends Spell(
    "Card",
    ManaCost(0),
    Type.Instant,
    Nil,
    destroy(target(Creature(withPower(4.orGreater)))))

  val ThreeFive = vanillaCreature(3, 5)
  val FourTwo = vanillaCreature(4, 2)
  val FiveFive = vanillaCreature(5, 5)

  override def testCards = Seq(TestCard, ThreeFive, FourTwo, FiveFive)

  "power X or greater filter" should {
    "have correct oracle text" in {
      TestCard.text mustEqual "Destroy target creature with power 4 or greater."
    }

    "move the creature to the graveyard" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCard)
        .setBattlefield(playerTwo, Seq(ThreeFive, FourTwo, FiveFive))

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestCard)

      manager.currentChoice must beSome(beTargetChoice.forPlayer(playerOne).withAvailableTargets(FourTwo, FiveFive))
    }
  }
}
