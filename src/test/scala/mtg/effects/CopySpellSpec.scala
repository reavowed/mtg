package mtg.effects

import mtg.abilities.TriggeredAbilityDefinition
import mtg.abilities.builder.InstructionBuilder._
import mtg.cards.patterns.{ArtifactCard, SpellCard}
import mtg.core.types.Type
import mtg.game.objects.{AbilityOnTheStack, CopyOfSpell}
import mtg.game.turns.TurnPhase
import mtg.helpers.SpecWithTestCards
import mtg.instructions.actions.{Copy, DrawACard, GainLife}
import mtg.instructions.articles.A
import mtg.instructions.conditions.When
import mtg.instructions.nouns.{Spell, You}
import mtg.parts.costs.ManaCost

class CopySpellSpec extends SpecWithTestCards {
  object TestCopyCard extends SpellCard(
    "Copy Card",
    ManaCost(0),
    Type.Instant,
    Nil,
    Copy(target(spell)))
  object TestGainOneLifeCard extends SpellCard(
    "Lifegain Card",
    ManaCost(0),
    Type.Instant,
    Nil,
    you(GainLife(1)))

  object TestCopyTriggerArtifact extends ArtifactCard(
    "Copy Trigger Artifact",
    ManaCost(0),
    When(You, Copy, A(Spell))(DrawACard))

  override def testCards = Seq(TestCopyCard, TestGainOneLifeCard, TestCopyTriggerArtifact)

  "copying a spell" should {
    "duplicate a simple spell" in {
      val initialState = emptyGameObjectState.setHand(playerOne, Seq(TestCopyCard, TestGainOneLifeCard))

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestGainOneLifeCard)
      manager.castSpell(playerOne, TestCopyCard)
      manager.chooseCard(playerOne, TestGainOneLifeCard)
      manager.resolveNext()

      manager.gameState.gameObjectState.stack.size mustEqual 2
      manager.gameState.gameObjectState.stack(1).underlyingObject must beAnInstanceOf[CopyOfSpell]
      manager.gameState.gameObjectState.stack(1).underlyingObject.baseCharacteristics.name must beSome(TestGainOneLifeCard.name)
    }

    "trigger an appropriate ability" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(TestCopyCard, TestGainOneLifeCard))
        .setBattlefield(playerOne, TestCopyTriggerArtifact)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestGainOneLifeCard)
      manager.castSpell(playerOne, TestCopyCard)
      manager.chooseCard(playerOne, TestGainOneLifeCard)
      manager.resolveNext()

      manager.gameState.gameObjectState.stack.size mustEqual 3
      manager.gameState.gameObjectState.stack(2).underlyingObject must beAnInstanceOf[AbilityOnTheStack]
      manager.gameState.gameObjectState.stack(2).underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition mustEqual
        TestCopyTriggerArtifact.textParagraphs.head
    }
  }

  "a copied spell" should {
    "have the same effects" in {
      val initialState = emptyGameObjectState.setHand(playerOne, Seq(TestCopyCard, TestGainOneLifeCard))

      val initialLifeTotal = initialState.lifeTotals(playerOne)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestGainOneLifeCard)
      manager.castSpell(playerOne, TestCopyCard)
      manager.chooseCard(playerOne, TestGainOneLifeCard)
      manager.passUntilStackEmpty()

      manager.gameState.gameObjectState.lifeTotals(playerOne) mustEqual initialLifeTotal + 2
    }
  }

}
