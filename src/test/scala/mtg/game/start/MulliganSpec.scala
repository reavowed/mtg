package mtg.game.start

import mtg.{SpecWithGameObjectState, SpecWithGameStateManager}
import mtg.game.PlayerIdentifier
import mtg.game.objects.{CardObject, GameObject, GameObjectState}
import mtg.game.start.mulligans.{MulliganChoice, ReturnCardsToLibraryChoice}
import mtg.game.state.{GameState, GameStateManager}
import mtg.game.turns.{PriorityChoice, TurnPhase, TurnStep}
import org.specs2.matcher.MatchResult

class MulliganSpec extends SpecWithGameStateManager {
  def checkAllCardsAreNewObjects(gameObjects: Seq[GameObject], previousGameObjectState: GameObjectState): MatchResult[_] = {
    foreach(gameObjects.ofType[CardObject])(cardObject => cardObject.objectId.sequentialId.must(beGreaterThanOrEqualTo(previousGameObjectState.nextObjectId)))
  }

  def checkCardsAreDifferent(firstGameObjects: Seq[GameObject], secondGameObjects: Seq[GameObject]): MatchResult[_] = {
    firstGameObjects.ofType[CardObject].map(_.card) must not(contain(eachOf(secondGameObjects.ofType[CardObject].map(_.card): _*)))
  }

  def checkLibraryAndHandAreTheSame(beforeState: GameObjectState, afterState: GameObjectState, player: PlayerIdentifier) = {
    beforeState.hands(player) mustEqual afterState.hands(player)
    beforeState.libraries(player) mustEqual afterState.libraries(player)
  }

  def checkMulliganAndNewHandDrawn(beforeState: GameObjectState, afterState: GameObjectState, player: PlayerIdentifier) = {
    checkAllCardsAreNewObjects(afterState.hands(player), beforeState)
    checkAllCardsAreNewObjects(afterState.libraries(player), beforeState)
    checkCardsAreDifferent(afterState.hands(player), beforeState.hands(player))
    afterState.hands(player).size mustEqual 7
  }

  def checkGameStarted(gameState: GameState) = {
    gameState.currentTurnNumber mustEqual 1
    gameState.currentPhase must beSome[TurnPhase](TurnPhase.BeginningPhase)
    gameState.currentStep must beSome[TurnStep](TurnStep.UpkeepStep)
    gameState.pendingActions.head mustEqual PriorityChoice(players)
  }

  "mulligan action" should {
    "draw all players cards if no mulligans have been taken" in {
      val pregameState = gameObjectStateWithInitialLibrariesOnly
      val finalGameState = runAction(StartGameAction, pregameState)

      finalGameState.gameObjectState.hands(playerOne).map(_.card) must contain(exactly(pregameState.libraries(playerOne).take(7).map(_.card): _*))
      finalGameState.gameObjectState.hands(playerTwo).map(_.card) must contain(exactly(pregameState.libraries(playerTwo).take(7).map(_.card): _*))
    }

    "begin the game if both players keep" in {
      val manager = createGameStateManager(gameObjectStateWithInitialLibrariesAndHands, StartGameAction)
      val beforeMulliganGameObjectState = manager.currentGameState.gameObjectState
      manager.handleDecision("K", playerOne)
      manager.handleDecision("K", playerTwo)

      val finalGameState = manager.currentGameState
      finalGameState.gameObjectState mustEqual beforeMulliganGameObjectState
      checkGameStarted(finalGameState)
    }

    "draw both players new cards if both players have mulliganed" in {
      val manager = createGameStateManager(gameObjectStateWithInitialLibrariesAndHands, StartGameAction)
      val beforeMulliganGameObjectState = manager.currentGameState.gameObjectState
      manager.handleDecision("M", playerOne)
      manager.handleDecision("M", playerTwo)

      val finalGameState = manager.currentGameState
      checkMulliganAndNewHandDrawn(beforeMulliganGameObjectState, finalGameState.gameObjectState, playerOne)
      checkMulliganAndNewHandDrawn(beforeMulliganGameObjectState, finalGameState.gameObjectState, playerTwo)
      finalGameState.pendingActions.head mustEqual MulliganChoice(playerOne, 1)
    }

    "only draw one player new cards if other player has kept" in {
      val manager = createGameStateManager(gameObjectStateWithInitialLibrariesAndHands, StartGameAction)
      val beforeMulliganGameObjectState = manager.currentGameState.gameObjectState
      manager.handleDecision("K", playerOne)
      manager.handleDecision("M", playerTwo)

      val finalGameState = manager.currentGameState
      checkLibraryAndHandAreTheSame(beforeMulliganGameObjectState, finalGameState.gameObjectState, playerOne)
      checkMulliganAndNewHandDrawn(beforeMulliganGameObjectState, finalGameState.gameObjectState, playerTwo)
      finalGameState.pendingActions.head mustEqual MulliganChoice(playerTwo, 1)
    }

    "require a card to be put back after mulliganing once" in {
      val manager = createGameStateManager(gameObjectStateWithInitialLibrariesAndHands, StartGameAction)
      manager.handleDecision("K", playerOne)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("K", playerTwo)

      val finalGameState = manager.currentGameState
      finalGameState.pendingActions.head mustEqual ReturnCardsToLibraryChoice(playerTwo, 1)
    }

    "only allow seven mulligans" in {
      val manager = createGameStateManager(gameObjectStateWithInitialLibrariesAndHands, StartGameAction)
      manager.handleDecision("K", playerOne)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("M", playerTwo)
      manager.handleDecision("M", playerTwo)

      val finalGameState = manager.currentGameState
      finalGameState.pendingActions.head mustEqual ReturnCardsToLibraryChoice(playerTwo, 7)
    }
  }
}
