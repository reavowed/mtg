package mtg.game.start

import mtg._
import mtg.game.objects.{GameObject, GameObjectState}
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameState, NewChoice}
import mtg.game.turns.{TurnPhase, TurnStep}
import mtg.game.{PlayerId, Zone}
import org.specs2.matcher.MatchResult

class MulliganSpec extends SpecWithGameStateManager {
  def checkAllCardsAreNewObjects(gameObjects: Seq[GameObject], previousGameObjectState: GameObjectState): MatchResult[_] = {
    foreach(gameObjects)(cardObject => cardObject.objectId.sequentialId.must(beGreaterThanOrEqualTo(previousGameObjectState.nextObjectId)))
  }

  def checkCardsAreDifferent(firstGameObjects: Seq[GameObject], secondGameObjects: Seq[GameObject]): MatchResult[_] = {
    firstGameObjects.map(_.underlyingObject) must not(contain(eachOf(secondGameObjects.map(_.underlyingObject): _*)))
  }

  def checkLibraryAndHandAreTheSame(beforeState: GameObjectState, afterState: GameObjectState, player: PlayerId) = {
    beforeState.hands(player) mustEqual afterState.hands(player)
    beforeState.libraries(player) mustEqual afterState.libraries(player)
  }

  def checkMulliganAndNewHandDrawn(beforeState: GameObjectState, afterState: GameObjectState, player: PlayerId) = {
    checkAllCardsAreNewObjects(afterState.hands(player), beforeState)
    checkAllCardsAreNewObjects(afterState.libraries(player), beforeState)
    checkCardsAreDifferent(afterState.hands(player), beforeState.hands(player))
    afterState.hands(player).size mustEqual 7
  }

  def checkGameStarted(gameState: GameState) = {
    gameState.currentTurnNumber mustEqual 1
    gameState.currentPhase must beSome[TurnPhase](TurnPhase.BeginningPhase)
    gameState.currentStep must beSome[TurnStep](TurnStep.UpkeepStep)
    gameState.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne))
  }

  "mulligan action" should {
    "draw all players cards if no mulligans have been taken" in {
      val pregameState = gameObjectStateWithInitialLibrariesOnly
      val finalGameState = runAction(MulligansAction(players, 0), pregameState)

      finalGameState.gameObjectState.hands(playerOne).map(_.underlyingObject) must contain(exactly(pregameState.libraries(playerOne).take(7).map(_.underlyingObject): _*))
      finalGameState.gameObjectState.hands(playerTwo).map(_.underlyingObject) must contain(exactly(pregameState.libraries(playerTwo).take(7).map(_.underlyingObject): _*))
    }

    "begin the game if both players keep" in {
      val manager = createGameStateManager(gameObjectStateWithInitialLibrariesAndHands, MulligansAction(players, 0))
      val beforeMulliganGameObjectState = manager.gameState.gameObjectState
      manager.handleDecision("K", playerOne)
      manager.handleDecision("K", playerTwo)

      val finalGameState = manager.gameState
      finalGameState.gameObjectState mustEqual beforeMulliganGameObjectState
      checkGameStarted(finalGameState)
    }

    "draw both players new cards if both players have mulliganed" in {
      val manager = createGameStateManager(gameObjectStateWithInitialLibrariesAndHands, MulligansAction(players, 0))
      val beforeMulliganGameObjectState = manager.gameState.gameObjectState
      manager.handleDecision("M", playerOne)
      manager.handleDecision("M", playerTwo)

      val finalGameState = manager.gameState
      checkMulliganAndNewHandDrawn(beforeMulliganGameObjectState, finalGameState.gameObjectState, playerOne)
      checkMulliganAndNewHandDrawn(beforeMulliganGameObjectState, finalGameState.gameObjectState, playerTwo)
      finalGameState.currentChoice must beSome(MulliganChoice(playerOne, 1))
    }

    "only draw one player new cards if other player has kept" in {
      val manager = createGameStateManager(gameObjectStateWithInitialLibrariesAndHands, MulligansAction(players, 0))
      val beforeMulliganGameObjectState = manager.gameState.gameObjectState
      manager.handleDecision("K", playerOne)
      manager.handleDecision("M", playerTwo)

      val finalGameState = manager.gameState
      checkLibraryAndHandAreTheSame(beforeMulliganGameObjectState, finalGameState.gameObjectState, playerOne)
      checkMulliganAndNewHandDrawn(beforeMulliganGameObjectState, finalGameState.gameObjectState, playerTwo)
      finalGameState.currentChoice must beSome(MulliganChoice(playerTwo, 1))
    }

    "require a card to be put back after mulliganing once" in {
      val manager = createGameStateManager(gameObjectStateWithInitialLibrariesAndHands, MulligansAction(players, 0))
      manager.handleDecision("K", playerOne)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("K", playerTwo)

      val finalGameState = manager.gameState
      finalGameState.allCurrentActions must contain(beAnInstanceOf[ReturnCardsToLibrary])
      finalGameState.currentChoice must beSome(beLike[NewChoice[_]] {
        case ChooseCardsInHand(`playerTwo`, 1) => ok
      })
    }

    "only allow seven mulligans" in {
      val manager = createGameStateManager(gameObjectStateWithInitialLibrariesAndHands, MulligansAction(players, 0))
      manager.handleDecision("K", playerOne)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("M", playerTwo)

      val finalGameState = manager.gameState
      finalGameState.allCurrentActions must contain(beAnInstanceOf[ReturnCardsToLibrary])
      finalGameState.currentChoice must beSome(beLike[NewChoice[_]] {
        case ChooseCardsInHand(`playerTwo`, 7) => ok
      })
    }

    "log events" in {
      val manager = createGameStateManager(gameObjectStateWithInitialLibrariesAndHands, MulligansAction(players, 0))
      manager.handleDecision("K", playerOne)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("K", playerTwo)
      manager.handleDecision(Zone.Hand(playerTwo)(manager.gameState).head.objectId.toString, playerTwo)

      manager.gameState.gameHistory.logEvents.map(_.logEvent) must contain(allOf[LogEvent](
        LogEvent.KeepHand(playerOne, 7),
        LogEvent.Mulligan(playerTwo, 6),
        LogEvent.KeepHand(playerTwo, 6),
        LogEvent.ReturnCardsToLibrary(playerTwo, 1)
      ).inOrder)
    }
  }
}
