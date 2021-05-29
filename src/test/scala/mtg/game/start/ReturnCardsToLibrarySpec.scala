package mtg.game.start

import mtg.SpecWithGameStateManager
import mtg.game.start.mulligans.ReturnCardsToLibraryChoice
import mtg.game.state.history.LogEvent

class ReturnCardsToLibrarySpec extends SpecWithGameStateManager {
  "returning cards to library" should {
    "put back one card" in {
      val beforeState = gameObjectStateWithInitialLibrariesAndHands
      val cardToPutBack = beforeState.hands(playerOne).head
      val choice = ReturnCardsToLibraryChoice(playerOne, 1)

      val manager = createGameStateManager(beforeState, choice)
      manager.handleDecision(cardToPutBack.objectId.sequentialId.toString, playerOne)
      val afterState = manager.currentGameState.gameObjectState

      val expectedHand = playerOne.hand(beforeState).filter(o => o != cardToPutBack)
      val expectedLibraryMatchers = playerOne.library(beforeState).map(typedEqualTo(_)) :+ beCardObject(cardToPutBack.card)
      playerOne.hand(afterState) must contain(exactly(expectedHand: _*))
      playerOne.library(afterState) must contain(exactly(expectedLibraryMatchers: _*).inOrder)
    }

    "put back two cards in correct order" in {
      val beforeState = gameObjectStateWithInitialLibrariesAndHands
      val firstCardToPutBack = beforeState.hands(playerOne)(6)
      val secondCardToPutBack = beforeState.hands(playerOne)(2)
      val choice = ReturnCardsToLibraryChoice(playerOne, 1)

      val manager = createGameStateManager(beforeState, choice)
      manager.handleDecision(Seq(firstCardToPutBack, secondCardToPutBack).map(_.objectId.sequentialId.toString).mkString(" "), playerOne)
      val afterState = manager.currentGameState.gameObjectState

      val expectedHand = playerOne.hand(beforeState).filter(o => o != firstCardToPutBack && o != secondCardToPutBack)
      val expectedLibraryMatchers = playerOne.library(beforeState).map(typedEqualTo(_)) :+ beCardObject(firstCardToPutBack.card) :+ beCardObject(secondCardToPutBack.card)
      playerOne.hand(afterState) must contain(exactly(expectedHand: _*))
      playerOne.library(afterState) must contain(exactly(expectedLibraryMatchers: _*).inOrder)
    }

    "log an event" in {
      val beforeState = gameObjectStateWithInitialLibrariesAndHands
      val cardToPutBack = beforeState.hands(playerOne).head
      val choice = ReturnCardsToLibraryChoice(playerOne, 1)

      val manager = createGameStateManager(beforeState, choice)
      manager.handleDecision(cardToPutBack.objectId.sequentialId.toString, playerOne)

      manager.currentGameState.gameHistory.logEvents.map(_.logEvent) mustEqual Seq(LogEvent.ReturnCardsToLibrary(playerOne, 1))
    }
  }

}
