package mtg.actions

import mtg.SpecWithGameStateManager
import mtg.cards.patterns.SpellCard
import mtg.core.types.Type
import mtg.game.turns.TurnPhase
import mtg.instructions.verbs.DrawACard
import mtg.parts.costs.ManaCost

class DrawCardSpec extends SpecWithGameStateManager {
  object TestCard extends SpellCard(
    "Card",
    ManaCost(0),
    Type.Instant,
    Nil,
    DrawACard)

  "draw card event" should {
    "move the top card of the library to the hand" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, TestCard)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestCard)
      manager.resolveNext()

      val cardThatShouldBeDrawn = playerOne.library(initialState).head.underlyingObject

      playerOne.hand(manager.gameState) must contain(exactly(beObject(cardThatShouldBeDrawn)))
      playerOne.library(manager.gameState) mustEqual playerOne.library(initialState).tail
    }

    "do nothing if library is empty" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, TestCard)
        .setLibrary(playerOne, Nil: _*)
      val event = DrawCardAction(playerOne)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestCard)
      manager.resolveNext()

      playerOne.hand(manager.gameState) must beEmpty
    }
  }
}
