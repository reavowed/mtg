package mtg.instructions.verbs

import mtg.SpecWithGameStateManager
import mtg.TestCards.vanillaCreature
import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.SpellCard
import mtg.core.types.Type
import mtg.core.types.Type.Creature
import mtg.game.turns.TurnPhase
import mtg.instructions.nounPhrases.You
import mtg.parts.costs.ManaCost

class MassGetSpec extends SpecWithGameStateManager {
  object TestCard extends SpellCard(
    "Card",
    ManaCost(0),
    Type.Instant,
    Nil,
    Creature(You(Control))(Get(1, 1), endOfTurn))

  val VanillaOneOne = vanillaCreature(1, 1)
  val VanillaTwoTwo = vanillaCreature(2, 2)

  "mass buff effect" should {
    "have correct oracle text" in {
      TestCard.text mustEqual "Creatures you control get +1/+1 until end of turn."
    }

    "apply filter correctly" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCard)
        .setBattlefield(playerOne, Seq(VanillaOneOne, VanillaTwoTwo))
        .setBattlefield(playerTwo, VanillaOneOne)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
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

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestCard)
      manager.resolveNext()
      manager.castSpell(playerOne, VanillaOneOne)
      manager.resolveNext()

      manager.getPermanent(VanillaOneOne, playerOne) must havePowerAndToughness(1, 1)
    }
  }
}
