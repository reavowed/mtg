package mtg.adjectives

import mtg.cards.patterns.{ArtifactCard, CreatureCard, SpellCard}
import mtg.helpers.SpecWithTestCards
import mtg.instructions.actions.{Cast, DrawACard}
import mtg.instructions.articles.A
import mtg.instructions.conditions.Whenever
import mtg.instructions.nouns.{Spell, You}
import mtg.parts.costs.ManaCost
import mtg.core.types.Type.{Instant, Sorcery}
import mtg.abilities.builder.TypeConversions._
import mtg.core.types.Type
import mtg.game.objects.AbilityOnTheStack
import mtg.game.turns.TurnPhase
import mtg.instructions.joiners.Or

class TypeAdjectiveSpec extends SpecWithTestCards {
  object TestInstantCard extends SpellCard("Test Instant", ManaCost(0), Type.Instant, Nil)
  object TestSorceryCard extends SpellCard("Test Sorcery", ManaCost(0), Type.Sorcery, Nil)
  object TestCreatureCard extends CreatureCard("Test Creature", ManaCost(0), Nil, Nil, (1, 1))
  object TestCastInstantTriggerArtifact extends ArtifactCard(
    "Cast Trigger Artifact",
    ManaCost(0),
    Whenever(You, Cast, A(Instant(Spell)))(DrawACard))
  object TestCastInstantOrSorceryTriggerArtifact extends ArtifactCard(
    "Cast Trigger Artifact",
    ManaCost(0),
    Whenever(You, Cast, A(Or(Instant, Sorcery)(Spell)))(DrawACard))

  override def testCards = Seq(TestInstantCard, TestSorceryCard, TestCreatureCard, TestCastInstantTriggerArtifact, TestCastInstantOrSorceryTriggerArtifact)

  "card with a trigger with an instant type filter" should {
    "have correct oracle text" in {
      TestCastInstantTriggerArtifact.text mustEqual "Whenever you cast an instant spell, draw a card."
    }
  }

  "cast trigger with instant type filter" should {
    "be triggered by casting an instant" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(TestInstantCard))
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
        .setHand(playerOne, Seq(TestSorceryCard))
        .setBattlefield(playerOne, TestCastInstantTriggerArtifact)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestSorceryCard)

      manager.gameState.gameObjectState.stack.size mustEqual 1
    }

    "not be triggered by casting a creature" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(TestCreatureCard))
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
        .setHand(playerOne, Seq(TestInstantCard))
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
        .setHand(playerOne, Seq(TestSorceryCard))
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
        .setHand(playerOne, Seq(TestCreatureCard))
        .setBattlefield(playerOne, TestCastInstantOrSorceryTriggerArtifact)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestCreatureCard)

      manager.gameState.gameObjectState.stack.size mustEqual 1
    }
  }
}
