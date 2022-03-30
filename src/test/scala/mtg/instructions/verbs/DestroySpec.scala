package mtg.instructions.verbs

import mtg.{SpecWithGameStateManager, TestCardCreation}
import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.SpellCard
import mtg.core.types.Type
import mtg.core.types.Type.Creature
import mtg.core.zones.Zone
import mtg.game.turns.TurnPhase
import mtg.instructions.nounPhrases.Target
import mtg.parts.costs.ManaCost

class DestroySpec extends SpecWithGameStateManager with TestCardCreation {
  object TestCard extends SpellCard(
    "Card",
    ManaCost(0),
    Type.Instant,
    Nil,
    Destroy(Target(Creature)))

  val VanillaOneOne = vanillaCreature(1, 1)

  "destroy effect" should {
    "have correct oracle text" in {
      TestCard.text mustEqual "Destroy target creature."
    }

    "move the creature to the graveyard" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCard)
        .setBattlefield(playerTwo, VanillaOneOne)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestCard)
      manager.chooseCard(playerOne, VanillaOneOne)
      manager.resolveNext()

      Zone.Battlefield(manager.gameState) must beEmpty
      playerTwo.graveyard(manager.gameState) must contain(exactly(beCardObject(VanillaOneOne)))
    }
  }
}
