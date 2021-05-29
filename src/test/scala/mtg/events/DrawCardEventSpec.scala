package mtg.events

import mtg.SpecWithGameStateManager

class DrawCardEventSpec extends SpecWithGameStateManager {

  "draw card event" should {
    "move the top card of the library to the hand" in {
      val event = DrawCardEvent(playerOne)
      val beforeState = gameObjectStateWithInitialLibrariesAndHands

      val afterState = runAction(event, beforeState).gameObjectState

      val cardThatShouldBeDrawn = playerOne.library(beforeState).head.card
      val expectedCardMatchers = playerOne.hand(beforeState).map(typedEqualTo(_)) :+ beCardObject(cardThatShouldBeDrawn)

      playerOne.hand(afterState) must contain(exactly(expectedCardMatchers: _*))
      playerOne.library(afterState) mustEqual playerOne.library(beforeState).tail
    }

    "do nothing if library is empty" in {
      val beforeState = gameObjectStateWithInitialLibrariesAndHands.setLibrary(playerOne, Nil)
      val event = DrawCardEvent(playerOne)

      val afterState = runAction(event, beforeState).gameObjectState

      afterState mustEqual beforeState
    }
  }
}
