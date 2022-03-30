package mtg.game.actions

import mtg.SpecWithGameStateManager
import mtg.cards.patterns.CreatureCard
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.parts.costs.ManaCost

class CastCreatureSpec extends SpecWithGameStateManager {
  val Creature = new CreatureCard("Creature", ManaCost(), Nil, Nil, (1, 1))

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
