package mtg.triggers

import mtg.abilities.builder.TypeConversions._
import mtg.core.types.Type.Creature
import mtg.game.objects.AbilityOnTheStack
import mtg.game.turns.TurnPhase
import mtg.instructions.articles.A
import mtg.instructions.conditions.When
import mtg.instructions.nounPhrases.{CardName, Target}
import mtg.instructions.verbs.{Destroy, Die, DrawACard, Exile}
import mtg.{SpecWithGameStateManager, TestCardCreation}

class DieTriggerSpec extends SpecWithGameStateManager with TestCardCreation {
  val CreatureWithDiesTrigger = zeroManaCreature(
    When(CardName, Die)(DrawACard),
    (1, 1))
  val ArtifactWithDiesTrigger = artifactWithTrigger(
    When(A(Creature), Die)(DrawACard))
  val VanillaCreature = vanillaCreature(1, 1)
  val DestroySpell = simpleInstantSpell(Destroy(Target(Creature)))
  val ExileSpell = simpleInstantSpell(Exile(Target(Creature)))

  "dies trigger on an artifact" should {
    "trigger when a creature dies" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, DestroySpell)
        .setBattlefield(playerOne, ArtifactWithDiesTrigger, VanillaCreature)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, DestroySpell)
      manager.chooseCard(playerOne, VanillaCreature)
      manager.resolveNext()

      manager.gameState.gameObjectState.stack.size mustEqual 1
      manager.gameState.gameObjectState.stack(0).underlyingObject must beAnInstanceOf[AbilityOnTheStack]
      manager.gameState.gameObjectState.stack(0).underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition mustEqual
        ArtifactWithDiesTrigger.textParagraphs.head
    }
  }

  "self-die trigger an a creature" should {
    "trigger when the creature dies" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, DestroySpell)
        .setBattlefield(playerOne, CreatureWithDiesTrigger)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, DestroySpell)
      manager.chooseCard(playerOne, CreatureWithDiesTrigger)
      manager.resolveNext()

      manager.gameState.gameObjectState.stack.size mustEqual 1
      manager.gameState.gameObjectState.stack(0).underlyingObject must beAnInstanceOf[AbilityOnTheStack]
      manager.gameState.gameObjectState.stack(0).underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition mustEqual
        CreatureWithDiesTrigger.textParagraphs.head
    }
    // TODO: Not on discard
  }

  // TODO: Creature dying sees another creature dying at the same time
}
