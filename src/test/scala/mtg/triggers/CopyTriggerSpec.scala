package mtg.triggers

import mtg.SpecWithGameStateManager
import mtg.cards.patterns.ArtifactCard
import mtg.instructions.verbs.CopySpellSpec.{TestCopyCard, TestGainOneLifeCard}
import mtg.game.objects.AbilityOnTheStack
import mtg.game.turns.TurnPhase
import mtg.instructions.articles.A
import mtg.instructions.conditions.Whenever
import mtg.instructions.nounPhrases.You
import mtg.instructions.nouns.Spell
import mtg.instructions.verbs.{Copy, DrawACard}
import mtg.parts.costs.ManaCost

class CopyTriggerSpec extends SpecWithGameStateManager {

  object TestCopyTriggerArtifact extends ArtifactCard(
    "Copy Trigger Artifact",
    ManaCost(0),
    Whenever(You, Copy, A(Spell))(DrawACard))

  "a copy trigger" should {
    "trigger off copying a spell"  in {
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

}
