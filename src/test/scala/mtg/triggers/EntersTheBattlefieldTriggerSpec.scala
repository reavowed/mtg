package mtg.triggers

import mtg.game.objects.AbilityOnTheStack
import mtg.game.turns.TurnPhase
import mtg.instructions.articles.A
import mtg.instructions.conditions.Whenever
import mtg.instructions.nounPhrases.{CardName, You}
import mtg.instructions.nouns.Spell
import mtg.instructions.verbs.{Cast, DrawACard, EntersTheBattlefield, GainLife}
import mtg.{SpecWithGameStateManager, TestCardCreation}

class EntersTheBattlefieldTriggerSpec extends SpecWithGameStateManager with TestCardCreation {
  val TestCreature = zeroManaCreature(
    Whenever(CardName, EntersTheBattlefield)(DrawACard),
    (1, 1))

  "an enters the battlefield trigger on a creature" should {
    "trigger when that creature spell resolves"  in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCreature)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestCreature)
      manager.resolveNext()

      manager.gameState.gameObjectState.stack.size mustEqual 1
      manager.gameState.gameObjectState.stack.head.underlyingObject must beAnInstanceOf[AbilityOnTheStack]
      manager.gameState.gameObjectState.stack.head.underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition mustEqual
        TestCreature.textParagraphs.head
    }
  }

}
