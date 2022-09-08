package mtg.instructions.filters

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.builder.TypeConversions._
import mtg.definitions.types.Type.Creature
import mtg.game.turns.TurnPhase
import mtg.instructions.nounPhrases.Target
import mtg.instructions.suffixDescriptors.WithPower
import mtg.instructions.verbs.Destroy
import mtg.{SpecWithGameStateManager, TestCardCreation}

class PowerConstantOrGreaterFilterSpec extends SpecWithGameStateManager with TestCardCreation {
  val TestCard = simpleInstantSpell(Destroy(Target(Creature(WithPower(4.orGreater)))))

  val ThreeFive = vanillaCreature(3, 5)
  val FourTwo = vanillaCreature(4, 2)
  val FiveFive = vanillaCreature(5, 5)

  "power X or greater filter" should {
    "have correct oracle text" in {
      TestCard.text mustEqual "Destroy target creature with power 4 or greater."
    }

    "move the creature to the graveyard" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCard)
        .setBattlefield(playerTwo, ThreeFive, FourTwo, FiveFive)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestCard)

      manager.currentChoice must beSome(beTargetChoice.forPlayer(playerOne).withAvailableTargets(FourTwo, FiveFive))
    }
  }
}
