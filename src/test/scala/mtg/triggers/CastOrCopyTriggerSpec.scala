package mtg.triggers

import mtg.{SpecWithGameStateManager, TestCardCreation}
import mtg.game.objects.AbilityOnTheStack
import mtg.game.turns.TurnPhase
import mtg.instructions.articles.A
import mtg.instructions.conditions.Whenever
import mtg.instructions.joiners.Or
import mtg.instructions.nounPhrases.{Target, You}
import mtg.instructions.nouns.Spell
import mtg.instructions.verbs.{Cast, Copy, DrawACard, GainLife}

class CastOrCopyTriggerSpec extends SpecWithGameStateManager with TestCardCreation {
  val TestCopyCard = simpleInstantSpell(Copy(Target(Spell)))
  val TestGainOneLifeCard = simpleInstantSpell(You(GainLife(1)))
  val TestCastOrCopyTriggerArtifact = artifactWithTrigger(Whenever(You, Or(Cast, Copy), A(Spell))(DrawACard))

  "a cast or copy trigger" should {
    "trigger off casting a spell"  in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestGainOneLifeCard)
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
        .setHand(playerOne, TestGainOneLifeCard, TestCopyCard)
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
