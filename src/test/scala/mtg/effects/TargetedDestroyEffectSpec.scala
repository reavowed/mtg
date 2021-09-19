package mtg.effects

import mtg.TestCards.vanillaCreature
import mtg.abilities.builder.EffectBuilder._
import mtg.cards.patterns.Spell
import mtg.characteristics.types.Type
import mtg.characteristics.types.Type.Creature
import mtg.game.Zone
import mtg.game.turns.{StartNextTurnAction, TurnPhase}
import mtg.helpers.SpecWithTestCards
import mtg.parts.costs.ManaCost

class TargetedDestroyEffectSpec extends SpecWithTestCards {
  object TestCard extends Spell(
    "Card",
    ManaCost(0),
    Type.Instant,
    Nil,
    destroy(target(Creature)))

  val VanillaOneOne = vanillaCreature(1, 1)

  override def testCards = Seq(TestCard, VanillaOneOne)

  "destroy effect" should {
    "have correct oracle text" in {
      TestCard.text mustEqual "Destroy target creature."
    }

    "move the creature to the graveyard" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCard)
        .setBattlefield(playerTwo, VanillaOneOne)

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestCard)
      manager.chooseCard(playerOne, VanillaOneOne)
      manager.resolveNext()

      Zone.Battlefield(manager.gameState) must beEmpty
      playerTwo.graveyard(manager.gameState) must contain(exactly(beCardObject(VanillaOneOne)))
    }
  }
}
