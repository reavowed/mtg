package mtg.game.start

import mtg.SpecWithGameStateManager
import mtg.game.state.history.LogEvent

class ReturnCardsToLibraryActionSpec extends SpecWithGameStateManager {
  "returning cards to library" should {
    "put back one card" in {
      val beforeState = gameObjectStateWithInitialLibrariesAndHands
      val cardToPutBack = beforeState.hands(playerOne).head
      val choice = ReturnCardsToLibraryChoice(playerOne, 1)

      val manager = createGameStateManager(beforeState, choice)
      manager.handleDecision(cardToPutBack.objectId.sequentialId.toString, playerOne)
      val afterState = manager.gameState.gameObjectState

      val expectedHand = playerOne.hand(beforeState).filter(o => o != cardToPutBack)
      val expectedLibraryMatchers = playerOne.library(beforeState).map(typedEqualTo(_)) :+ beObject(cardToPutBack.underlyingObject)
      playerOne.hand(afterState) must contain(exactly(expectedHand: _*))
      playerOne.library(afterState) must contain(exactly(expectedLibraryMatchers: _*).inOrder)
    }

    "put back two cards in correct order" in {
      val beforeState = gameObjectStateWithInitialLibrariesAndHands
      val firstCardToPutBack = beforeState.hands(playerOne)(6)
      val secondCardToPutBack = beforeState.hands(playerOne)(2)
      val choice = ReturnCardsToLibraryChoice(playerOne, 2)

      val manager = createGameStateManager(beforeState, choice)
      manager.handleDecision(Seq(firstCardToPutBack, secondCardToPutBack).map(_.objectId.sequentialId.toString).mkString(" "), playerOne)
      val afterState = manager.gameState.gameObjectState

      val expectedHand = playerOne.hand(beforeState).filter(o => o != firstCardToPutBack && o != secondCardToPutBack)
      val expectedLibraryMatchers = playerOne.library(beforeState).map(typedEqualTo(_)) :+ beObject(firstCardToPutBack.underlyingObject) :+ beObject(secondCardToPutBack.underlyingObject)
      playerOne.hand(afterState) must contain(exactly(expectedHand: _*))
      playerOne.library(afterState) must contain(exactly(expectedLibraryMatchers: _*).inOrder)
    }

    "log an event" in {
      val beforeState = gameObjectStateWithInitialLibrariesAndHands
      val cardToPutBack = beforeState.hands(playerOne).head
      val choice = ReturnCardsToLibraryChoice(playerOne, 1)

      val manager = createGameStateManager(beforeState, choice)
      manager.handleDecision(cardToPutBack.objectId.sequentialId.toString, playerOne)

      manager.gameState.gameHistory.logEvents.map(_.logEvent) mustEqual Seq(LogEvent.ReturnCardsToLibrary(playerOne, 1))
    }
  }

}
