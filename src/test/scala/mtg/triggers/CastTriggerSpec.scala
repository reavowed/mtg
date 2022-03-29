package mtg.triggers

import mtg.cards.patterns.ArtifactCard
import mtg.effects.CopySpellSpec.{TestCopyCard, TestGainOneLifeCard}
import mtg.game.objects.AbilityOnTheStack
import mtg.game.turns.TurnPhase
import mtg.helpers.SpecWithTestCards
import mtg.instructions.verbs.{Cast, Copy, DrawACard}
import mtg.instructions.articles.A
import mtg.instructions.conditions.Whenever
import mtg.instructions.nouns.{Spell, You}
import mtg.parts.costs.ManaCost

class CastTriggerSpec extends SpecWithTestCards {

  object TestCastTriggerArtifact extends ArtifactCard(
    "Cast Trigger Artifact",
    ManaCost(0),
    Whenever(You, Cast, A(Spell))(DrawACard))

  override def testCards = Seq(TestGainOneLifeCard, TestCastTriggerArtifact)

  "a cast trigger" should {
    "trigger off casting a spell"  in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(TestGainOneLifeCard))
        .setBattlefield(playerOne, TestCastTriggerArtifact)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestGainOneLifeCard)

      manager.gameState.gameObjectState.stack.size mustEqual 2
      manager.gameState.gameObjectState.stack(1).underlyingObject must beAnInstanceOf[AbilityOnTheStack]
      manager.gameState.gameObjectState.stack(1).underlyingObject.asInstanceOf[AbilityOnTheStack].abilityDefinition mustEqual
        TestCastTriggerArtifact.textParagraphs.head
    }
  }

}
