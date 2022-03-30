package mtg.triggers

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.builder.TypeConversions._
import mtg.game.objects.AbilityOnTheStack
import mtg.game.turns.TurnStep
import mtg.instructions.conditions.At
import mtg.instructions.nounPhrases.{CardName, You}
import mtg.instructions.verbs.Get
import mtg.{SpecWithGameStateManager, TestCardCreation}

class TriggeredAbilitySourceReferenceSpec extends SpecWithGameStateManager with TestCardCreation {
  val TestCreature = zeroManaCreature(
    At(beginningOfCombat(You))(CardName(Get(+1, +1), endOfTurn)),
    (1, 1))

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
        TestCreature.name + " gets +1/+1 until end of turn."
    }

    "apply effects to its source" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, TestCreature)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.BeginningOfCombatStep)
      manager.resolveNext()


      manager.getState(manager.getCard(TestCreature)).characteristics.power must beSome(2)
      manager.getState(manager.getCard(TestCreature)).characteristics.toughness must beSome(2)
    }
  }
}
