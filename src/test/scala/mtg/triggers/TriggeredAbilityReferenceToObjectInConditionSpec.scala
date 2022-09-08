package mtg.triggers

import mtg.abilities.builder.TypeConversions._
import mtg.definitions.types.Type.Creature
import mtg.game.turns.TurnPhase
import mtg.instructions.articles.A
import mtg.instructions.conditions.Whenever
import mtg.instructions.nounPhrases.It
import mtg.instructions.verbs.{Destroy, EntersTheBattlefield}
import mtg.{SpecWithGameStateManager, TestCardCreation}

class TriggeredAbilityReferenceToObjectInConditionSpec extends SpecWithGameStateManager with TestCardCreation {
  val ArtifactWithTrigger = artifactWithTrigger(Whenever(A(Creature), EntersTheBattlefield)(Destroy(It)))
  val VanillaCreature = vanillaCreature(1, 1)

  "triggered ability with reference to creature in trigger condition" should {
    "apply to that creature" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, VanillaCreature)
        .setBattlefield(playerOne, ArtifactWithTrigger)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, VanillaCreature)
      manager.resolveNext()
      manager.resolveNext()

      playerOne.graveyard(manager.gameState) must contain(exactly(beCardObject(VanillaCreature)))
    }
  }
}
