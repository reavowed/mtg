package mtg.events

import mtg.SpecWithGameStateManager
import mtg.game.objects.CardObject

class DrawCardEventSpec extends SpecWithGameStateManager {

  "draw card event" should {
    "move the top card of the library to the hand" in {
      val event = DrawCardEvent(playerOne)
      val beforeState = gameObjectStateWithInitialLibrariesAndHands

      val manager = createGameStateManager(gameData, beforeState, event)

      val afterState = manager.currentGameState.gameObjectState

      val cardThatShouldBeDrawn = playerOne.library(beforeState).head.asInstanceOf[CardObject].card
      val expectedCardMatchers = playerOne.hand(beforeState).map(typedEqualTo(_)) :+ matchCardObject(cardThatShouldBeDrawn)

      playerOne.hand(afterState) must contain(exactly(expectedCardMatchers: _*))
      playerOne.library(afterState) mustEqual playerOne.library(beforeState).tail
    }

    "do nothing if library is empty" in {
      val beforeState = gameObjectStateWithInitialLibrariesAndHands.setLibrary(playerOne, Nil)
      val event = DrawCardEvent(playerOne)

      val manager = createGameStateManager(gameData, beforeState, event)

      val afterState = manager.currentGameState.gameObjectState
      afterState mustEqual beforeState
    }
  }
}
