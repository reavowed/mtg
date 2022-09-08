package mtg.instructions.verbs

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.builder.TypeConversions._
import mtg.definitions.types.Type.Creature
import mtg.game.turns.TurnPhase
import mtg.instructions.nounPhrases.You
import mtg.{SpecWithGameStateManager, TestCardCreation}

class MassGetSpec extends SpecWithGameStateManager with TestCardCreation {
  val BuffSpell = simpleInstantSpell(Creature(You(Control))(Get(1, 1), endOfTurn))
  val VanillaOneOne = vanillaCreature(1, 1)
  val VanillaTwoTwo = vanillaCreature(2, 2)

  "mass buff effect" should {
    "have correct oracle text" in {
      BuffSpell.text mustEqual "Creatures you control get +1/+1 until end of turn."
    }

    "apply filter correctly" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, BuffSpell)
        .setBattlefield(playerOne, VanillaOneOne, VanillaTwoTwo)
        .setBattlefield(playerTwo, VanillaOneOne)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, BuffSpell)
      manager.resolveNext()

      manager.getPermanent(VanillaOneOne, playerOne) must havePowerAndToughness(2, 2)
      manager.getPermanent(VanillaTwoTwo, playerOne) must havePowerAndToughness(3, 3)
      manager.getPermanent(VanillaOneOne, playerTwo) must havePowerAndToughness(1, 1)
    }

    "only apply to initial set of objects" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, BuffSpell, VanillaOneOne)
        .setBattlefield(playerOne, VanillaTwoTwo)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, BuffSpell)
      manager.resolveNext()
      manager.castSpell(playerOne, VanillaOneOne)
      manager.resolveNext()

      manager.getPermanent(VanillaOneOne, playerOne) must havePowerAndToughness(1, 1)
    }
  }
}
