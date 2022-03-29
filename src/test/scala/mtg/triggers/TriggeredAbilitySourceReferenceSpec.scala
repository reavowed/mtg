package mtg.triggers

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.CreatureCard
import mtg.game.objects.AbilityOnTheStack
import mtg.game.turns.TurnStep
import mtg.helpers.SpecWithTestCards
import mtg.instructions.conditions.At
import mtg.instructions.nounPhrases.{CardName, You}
import mtg.instructions.verbs.Get
import mtg.parts.costs.ManaCost

class TriggeredAbilitySourceReferenceSpec extends SpecWithTestCards {
  object TestCreature extends CreatureCard(
    "Test Creature",
    ManaCost(0),
    Nil,
    At(beginningOfCombat(You))(CardName(Get(+1, +1), endOfTurn)),
    (1, 1))

  override def testCards = Seq(TestCreature)

  "a triggered ability referring to its source" should {
    "have the correct text on the stack" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, TestCreature)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.BeginningOfCombatStep)

      manager.gameState.gameObjectState.stack.size mustEqual 1
      val stackObject = manager.gameState.gameObjectState.stack(0)
      stackObject.underlyingObject must beAnInstanceOf[AbilityOnTheStack]
      manager.gameState.gameObjectState.derivedState.stackObjectStates(stackObject.objectId).getText(manager.gameState) mustEqual
        "Test Creature gets +1/+1 until end of turn."
    }
  }
}
