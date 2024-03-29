package mtg.instructions.adjectives

import mtg.abilities.builder.TypeConversions._
import mtg.definitions.types.Type.Creature
import mtg.game.turns.TurnStep
import mtg.instructions.nounPhrases.Target
import mtg.instructions.verbs.Destroy
import mtg.{SpecWithGameStateManager, TestCardCreation}

class TappedAdjectiveSpec extends SpecWithGameStateManager with TestCardCreation {
  val TestCard = simpleInstantSpell(Destroy(Target(Tapped(Creature))))
  val TestCreatureOne = vanillaCreature(1, 1)
  val TestCreatureTwo = vanillaCreature(2, 2)

  "target with tapped modifier" should {
    "only allow choosing a tapped creature" in {
      val initialState = emptyGameObjectState
        .setHand(playerTwo, TestCard)
        .setBattlefield(playerOne, TestCreatureOne, TestCreatureTwo)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(TestCreatureOne)
      manager.passPriority(playerOne)
      manager.castSpell(playerTwo, TestCard)

      manager.currentChoice must beSome(beTargetChoice.withAvailableTargets(TestCreatureOne))
    }
  }
}
