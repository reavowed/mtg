package mtg.triggers

import mtg.cards.patterns.ArtifactCard
import mtg.effects.CopySpellSpec.{TestCopyCard, TestGainOneLifeCard}
import mtg.game.objects.AbilityOnTheStack
import mtg.game.turns.TurnPhase
import mtg.helpers.SpecWithTestCards
import mtg.instructions.verbs.{Cast, Copy, DrawACard}
import mtg.instructions.articles.A
import mtg.instructions.conditions.Whenever
import mtg.instructions.joiners.Or
import mtg.instructions.nounPhrases.You
import mtg.instructions.nouns.Spell
import mtg.parts.costs.ManaCost

class CastOrCopyTriggerSpec extends SpecWithTestCards {

  object TestCastOrCopyTriggerArtifact extends ArtifactCard(
    "Cast or Copy Trigger Artifact",
    ManaCost(0),
    Whenever(You, Or(Cast, Copy), A(Spell))(DrawACard))

  override def testCards = Seq(TestGainOneLifeCard, TestCopyCard, TestCastOrCopyTriggerArtifact)

  "a cast or copy trigger" should {
    "trigger off casting a spell"  in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(TestGainOneLifeCard))
        .setBattlefield(playerOne, TestCastOrCopyTriggerArtifact)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestGainOneLifeCard)

      manager.gameState.gameObjectState.stack.size mustEqual 2
      manager.gameState.gameObjectState.stack(1).underlyingObject must beAnInstanceOf[AbilityOnTheStack]
      manager.gameState.gameObjectState.stack(1).underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition mustEqual
        TestCastOrCopyTriggerArtifact.textParagraphs.head
    }

    "trigger off copying a spell"  in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(TestGainOneLifeCard, TestCopyCard))
        .setBattlefield(playerOne, TestCastOrCopyTriggerArtifact)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestGainOneLifeCard)
      manager.castSpell(playerOne, TestCopyCard)
      manager.chooseCard(playerOne, TestGainOneLifeCard)

      // Stack is now LifeGainCard, trigger, CopyCard, trigger
      manager.resolveNext()
      manager.resolveNext()

      manager.gameState.gameObjectState.stack.size mustEqual 4
      manager.gameState.gameObjectState.stack(3).underlyingObject must beAnInstanceOf[AbilityOnTheStack]
      manager.gameState.gameObjectState.stack(3).underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition mustEqual
        TestCastOrCopyTriggerArtifact.textParagraphs.head
    }
  }

}
