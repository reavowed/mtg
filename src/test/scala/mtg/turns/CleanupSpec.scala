package mtg.turns

import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.TurnStep
import mtg.instructions.nounPhrases.{AnyTarget, CardName}
import mtg.instructions.verbs.DealDamage
import mtg.{SpecWithGameStateManager, TestCardCreation}

class CleanupSpec extends SpecWithGameStateManager with TestCardCreation {
  val Creature = vanillaCreature(2, 2)
  val DamageSpell = simpleInstantSpell(CardName(DealDamage(1)(AnyTarget)))

  "cleanup step" should {
    "wear off damage" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, DamageSpell)
        .setBattlefield(playerTwo, Creature)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.castSpell(playerOne, DamageSpell)
      manager.chooseCard(playerOne, Creature)

      manager.passUntilStep(TurnStep.EndStep)
      manager.getPermanent(Creature).markedDamage mustEqual 1
      manager.passUntilTurn(2)
      manager.getPermanent(Creature).markedDamage mustEqual 0
    }
  }

}
