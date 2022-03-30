package mtg.triggers

import mtg.{SpecWithGameStateManager, TestCardCreation}
import mtg.game.objects.AbilityOnTheStack
import mtg.game.turns.TurnPhase
import mtg.instructions.articles.A
import mtg.instructions.conditions.Whenever
import mtg.instructions.nounPhrases.You
import mtg.instructions.nouns.Spell
import mtg.instructions.verbs.{Cast, DrawACard, GainLife}
import mtg.parts.costs.ManaCost

class CastTriggerSpec extends SpecWithGameStateManager with TestCardCreation {
  val TestGainOneLifeCard = simpleInstantSpell(You(GainLife(1)))
  val TestCastTriggerArtifact = artifactWithTrigger(Whenever(You, Cast, A(Spell))(DrawACard))

  "a cast trigger" should {
    "trigger off casting a spell"  in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestGainOneLifeCard)
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
