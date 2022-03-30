package mtg.actions

import mtg.game.turns.TurnPhase
import mtg.instructions.verbs.DrawACard
import mtg.{SpecWithGameStateManager, TestCardCreation}

class DrawCardSpec extends SpecWithGameStateManager with TestCardCreation {
  val TestCard = simpleInstantSpell(DrawACard)

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
