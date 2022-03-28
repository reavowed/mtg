package mtg.adjectives

import mtg.cards.patterns.{ArtifactCard, SpellCard}
import mtg.helpers.SpecWithTestCards
import mtg.instructions.actions.{Cast, DrawACard}
import mtg.instructions.articles.A
import mtg.instructions.conditions.When
import mtg.instructions.nouns.{Spell, You}
import mtg.parts.costs.ManaCost
import mtg.core.types.Type.Instant
import mtg.abilities.builder.TypeConversions._
import mtg.core.types.Type
import mtg.game.objects.AbilityOnTheStack
import mtg.game.turns.TurnPhase

class TypeAdjectiveSpec extends SpecWithTestCards {
  object TestInstantCard extends SpellCard("Test Instant", ManaCost(0), Type.Instant, Nil)
  object TestSorceryCard extends SpellCard("Test Sorcery", ManaCost(0), Type.Sorcery, Nil)
  object TestCastInstantTriggerArtifact extends ArtifactCard(
    "Cast Trigger Artifact",
    ManaCost(0),
    When(You, Cast, A(Instant(Spell)))(DrawACard))

  override def testCards = Seq(TestInstantCard, TestSorceryCard, TestCastInstantTriggerArtifact)

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
  }
}
