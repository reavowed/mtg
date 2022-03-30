package mtg.triggers

import mtg.{SpecWithGameStateManager, TestCardCreation}
import mtg.game.objects.AbilityOnTheStack
import mtg.game.turns.TurnPhase
import mtg.instructions.articles.A
import mtg.instructions.conditions.Whenever
import mtg.instructions.nounPhrases.{Target, You}
import mtg.instructions.nouns.Spell
import mtg.instructions.verbs.{Copy, DrawACard, GainLife}

class CopyTriggerSpec extends SpecWithGameStateManager with TestCardCreation {
  val TestCopyCard = simpleInstantSpell(Copy(Target(Spell)))
  val TestGainOneLifeCard = simpleInstantSpell(You(GainLife(1)))
  val TestCopyTriggerArtifact = artifactWithTrigger(Whenever(You, Copy, A(Spell))(DrawACard))

  "a copy trigger" should {
    "trigger off copying a spell"  in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCopyCard, TestGainOneLifeCard)
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
