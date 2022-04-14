package mtg.instructions.joiners

import mtg.game.turns.TurnPhase
import mtg.instructions.nounPhrases.You
import mtg.instructions.verbs.{DrawACard, GainLife}
import mtg.sets.alpha.cards.Plains
import mtg.{SpecWithGameStateManager, TestCardCreation}

class MaySpec extends SpecWithGameStateManager with TestCardCreation {
  "optional life gain instruction" should {
    val TestSpell = simpleSorcerySpell(You(May(GainLife(1))))

    "let you accept the choice" in {
      val initialState = emptyGameObjectState.setHand(playerOne, TestSpell)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestSpell)
      manager.resolveNext()
      manager.handleDecision("Yes", playerOne)

      manager.gameState.gameObjectState.lifeTotals(playerOne) mustEqual (initialState.lifeTotals(playerOne) + 1)
    }

    "let you decline the choice" in {
      val initialState = emptyGameObjectState.setHand(playerOne, TestSpell)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestSpell)
      manager.resolveNext()
      manager.handleDecision("No", playerOne)

      manager.gameState.gameObjectState.lifeTotals(playerOne) mustEqual initialState.lifeTotals(playerOne)
    }
  }
}
