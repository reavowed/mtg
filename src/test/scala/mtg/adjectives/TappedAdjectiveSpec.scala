package mtg.adjectives

import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.SpellCard
import mtg.core.types.Type
import mtg.core.types.Type.Creature
import mtg.game.turns.TurnStep
import mtg.instructions.adjectives.Tapped
import mtg.instructions.nounPhrases.Target
import mtg.instructions.verbs.Destroy
import mtg.parts.costs.ManaCost
import mtg.{SpecWithGameStateManager, TestCards}

class TappedAdjectiveSpec extends SpecWithGameStateManager {
  object TestCard extends SpellCard(
    "Card",
    ManaCost(0),
    Type.Instant,
    Nil,
    Destroy(Target(Tapped(Creature))))
  val TestCreatureOne = TestCards.vanillaCreature(1, 1)
  val TestCreatureTwo = TestCards.vanillaCreature(2, 2)

  "target with tapped modifier" should {
    "only allow choosing a tapped creature" in {
      val initialState = emptyGameObjectState
        .setHand(playerTwo, Seq(TestCard))
        .setBattlefield(playerOne, Seq(TestCreatureOne, TestCreatureTwo))

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, TestCreatureOne)
      manager.passPriority(playerOne)
      manager.castSpell(playerTwo, TestCard)

      manager.currentChoice must beSome(beTargetChoice.withAvailableTargets(TestCreatureOne))
    }
  }
}
