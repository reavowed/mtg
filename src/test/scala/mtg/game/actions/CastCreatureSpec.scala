package mtg.game.actions

import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.{SpecWithGameStateManager, TestCardCreation}

class CastCreatureSpec extends SpecWithGameStateManager with TestCardCreation {
  val Creature = vanillaCreature(1, 1)

  "casting a creature" should {
    "move the card to the battlefield on resolution" in {
      val initialState = emptyGameObjectState.setHand(playerOne, Creature)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      manager.castSpell(playerOne, Creature)
      manager.passPriority(playerOne)
      manager.passPriority(playerTwo)

      manager.gameState.gameObjectState.stack must beEmpty
      manager.gameState.gameObjectState.battlefield must contain(beCardObject(Creature))
    }
  }

}
