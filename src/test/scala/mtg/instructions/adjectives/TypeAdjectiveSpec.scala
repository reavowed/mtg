package mtg.instructions.adjectives

import mtg.abilities.builder.TypeConversions._
import mtg.definitions.types.Type.{Instant, Sorcery}
import mtg.game.objects.AbilityOnTheStack
import mtg.game.turns.TurnPhase
import mtg.instructions.articles.A
import mtg.instructions.conditions.Whenever
import mtg.instructions.joiners.Or
import mtg.instructions.nounPhrases.You
import mtg.instructions.nouns.Spell
import mtg.instructions.verbs.{Cast, DrawACard}
import mtg.{SpecWithGameStateManager, TestCardCreation}

class TypeAdjectiveSpec extends SpecWithGameStateManager with TestCardCreation {
  val TestInstantCard = simpleInstantSpell(Nil)
  val TestSorceryCard = simpleSorcerySpell(Nil)
  val TestCreatureCard = vanillaCreature(1, 1)
  val TestCastInstantTriggerArtifact = artifactWithTrigger(Whenever(You, Cast, A(Instant(Spell)))(DrawACard))
  val TestCastInstantOrSorceryTriggerArtifact = artifactWithTrigger(Whenever(You, Cast, A(Or(Instant, Sorcery)(Spell)))(DrawACard))

  "card with a trigger with an instant type filter" should {
    "have correct oracle text" in {
      TestCastInstantTriggerArtifact.text mustEqual "Whenever you cast an instant spell, draw a card."
    }
  }

  "cast trigger with instant type filter" should {
    "be triggered by casting an instant" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestInstantCard)
        .setBattlefield(playerOne, TestCastInstantTriggerArtifact)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestInstantCard)

      manager.gameState.gameObjectState.stack.size mustEqual 2
      manager.gameState.gameObjectState.stack(1).underlyingObject must beAnInstanceOf[AbilityOnTheStack]
      manager.gameState.gameObjectState.stack(1).underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition mustEqual
        TestCastInstantTriggerArtifact.textParagraphs.head
    }

    "not be triggered by casting a sorcery" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestSorceryCard)
        .setBattlefield(playerOne, TestCastInstantTriggerArtifact)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestSorceryCard)

      manager.gameState.gameObjectState.stack.size mustEqual 1
    }

    "not be triggered by casting a creature" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCreatureCard)
        .setBattlefield(playerOne, TestCastInstantTriggerArtifact)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestCreatureCard)

      manager.gameState.gameObjectState.stack.size mustEqual 1
    }
  }

  "cast trigger with instant or sorcery type filter" should {
    "be triggered by casting an instant" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestInstantCard)
        .setBattlefield(playerOne, TestCastInstantOrSorceryTriggerArtifact)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestInstantCard)

      manager.gameState.gameObjectState.stack.size mustEqual 2
      manager.gameState.gameObjectState.stack(1).underlyingObject must beAnInstanceOf[AbilityOnTheStack]
      manager.gameState.gameObjectState.stack(1).underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition mustEqual
        TestCastInstantOrSorceryTriggerArtifact.textParagraphs.head
    }

    "be triggered by casting a sorcery" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestSorceryCard)
        .setBattlefield(playerOne, TestCastInstantOrSorceryTriggerArtifact)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestSorceryCard)

      manager.gameState.gameObjectState.stack.size mustEqual 2
      manager.gameState.gameObjectState.stack(1).underlyingObject must beAnInstanceOf[AbilityOnTheStack]
      manager.gameState.gameObjectState.stack(1).underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition mustEqual
        TestCastInstantOrSorceryTriggerArtifact.textParagraphs.head
    }

    "not be triggered by casting a creature" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCreatureCard)
        .setBattlefield(playerOne, TestCastInstantOrSorceryTriggerArtifact)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestCreatureCard)

      manager.gameState.gameObjectState.stack.size mustEqual 1
    }
  }
}
