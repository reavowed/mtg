package mtg.game.start

import mtg.SpecWithGameObjectState
import mtg.data.cards.{Forest, Plains}
import mtg.data.sets.Strixhaven
import mtg.game.objects.{CardObject, GameObject, GameObjectState}
import mtg.game.start.mulligans.DrawAndMulliganAction
import mtg.game.turns.PriorityChoice
import mtg.game.{GameData, PlayerIdentifier}
import org.specs2.matcher.MatchResult

class MulliganSpec extends SpecWithGameObjectState {
  val p1 = PlayerIdentifier("P1")
  val p2 = PlayerIdentifier("P2")

  val plains = Strixhaven.getCard(Plains).get
  val forest = Strixhaven.getCard(Forest).get

  def checkAllCardsAreNewObjects(gameObjects: Seq[GameObject], previousGameObjectState: GameObjectState): MatchResult[_] = {
    foreach(gameObjects.ofType[CardObject])(cardObject => cardObject.objectId.sequentialId.must(beGreaterThanOrEqualTo(previousGameObjectState.nextObjectId)))
  }
  def checkCardsAreDifferent(firstGameObjects: Seq[GameObject], secondGameObjects: Seq[GameObject]): MatchResult[_] = {
    firstGameObjects.ofType[CardObject].map(_.card) must not(contain(eachOf(secondGameObjects.ofType[CardObject].map(_.card): _*)))
  }

  "draw starting hands action" should {
    "draw all players cards if no mulligans have been taken" in {
      val gameData = GameData(Seq(p1, p2))
      val p1Cards = Strixhaven.cardPrintings
      val p2Cards = Strixhaven.cardPrintings.reverse
      val initialGameObjectState = emptyGameObjectState(Seq(p1, p2)).setLibrary(p1, p1Cards).setLibrary(p2, p2Cards)

      val finalGameState = runAction(StartGameAction, gameData, initialGameObjectState)

      finalGameState.gameObjectState.hands(p1).map(_.card) must contain(exactly(initialGameObjectState.libraries(p1).take(7).map(_.card): _*))
      finalGameState.gameObjectState.hands(p2).map(_.card) must contain(exactly(initialGameObjectState.libraries(p2).take(7).map(_.card): _*))
    }

    "draw both players new cards if both players have mulliganed" in {
      val gameData = GameData(Seq(p1, p2))
      val p1Cards = Strixhaven.cardPrintings
      val p2Cards = Strixhaven.cardPrintings.reverse
      val initialGameObjectState = emptyGameObjectState(Seq(p1, p2)).setLibrary(p1, p1Cards).setLibrary(p2, p2Cards)

      val manager = createGameStateManager(gameData, initialGameObjectState, StartGameAction)
        .updateGameObjectState(s =>
          s.setLibrary(p1, p1Cards.drop(7))
            .setHand(p1, p1Cards.take(7))
            .setLibrary(p2, p2Cards.drop(7))
            .setHand(p2, p2Cards.take(7)))
      val beforeMulliganGameObjectState = manager.gameState.gameObjectState
      manager.handleDecision("M", p1)
      manager.handleDecision("M", p2)
      val finalGameState = manager.gameState

      checkAllCardsAreNewObjects(finalGameState.gameObjectState.hands(p1), beforeMulliganGameObjectState)
      checkAllCardsAreNewObjects(finalGameState.gameObjectState.hands(p2), beforeMulliganGameObjectState)
      checkAllCardsAreNewObjects(finalGameState.gameObjectState.libraries(p1), beforeMulliganGameObjectState)
      checkAllCardsAreNewObjects(finalGameState.gameObjectState.libraries(p2), beforeMulliganGameObjectState)

      finalGameState.gameObjectState.hands(p1).size mustEqual 7
      finalGameState.gameObjectState.hands(p2).size mustEqual 7
      checkCardsAreDifferent(finalGameState.gameObjectState.hands(p1), beforeMulliganGameObjectState.hands(p1))
      checkCardsAreDifferent(finalGameState.gameObjectState.hands(p2), beforeMulliganGameObjectState.hands(p2))
    }

    "only draw one player new cards if other player has kept" in {
      val gameData = GameData(Seq(p1, p2))
      val p1Cards = Strixhaven.cardPrintings
      val p2Cards = Strixhaven.cardPrintings.reverse
      val initialGameObjectState = emptyGameObjectState(Seq(p1, p2)).setLibrary(p1, p1Cards).setLibrary(p2, p2Cards)

      val manager = createGameStateManager(gameData, initialGameObjectState, StartGameAction)
        .updateGameObjectState(s => s.setLibrary(p1, p1Cards.drop(7)).setHand(p1, p1Cards.take(7)).setLibrary(p2, p2Cards.drop(7)).setHand(p2, p2Cards.take(7)))
      val beforeMulliganGameObjectState = manager.gameState.gameObjectState
      manager.handleDecision("K", p1)
      manager.handleDecision("M", p2)
      val finalGameState = manager.gameState

      finalGameState.gameObjectState.hands(p1) mustEqual beforeMulliganGameObjectState.hands(p1)
      finalGameState.gameObjectState.libraries(p1) mustEqual beforeMulliganGameObjectState.libraries(p1)

      checkAllCardsAreNewObjects(finalGameState.gameObjectState.hands(p2), beforeMulliganGameObjectState)
      checkAllCardsAreNewObjects(finalGameState.gameObjectState.libraries(p2), beforeMulliganGameObjectState)

      finalGameState.gameObjectState.hands(p2).size mustEqual 7
      checkCardsAreDifferent(finalGameState.gameObjectState.hands(p2), beforeMulliganGameObjectState.hands(p2))
    }
  }

  "mulligan action" should {
    "begin the game if both players keep" in {
      val gameData = GameData(Seq(p1, p2))
      val p1Cards = Strixhaven.cardPrintings
      val p2Cards = Strixhaven.cardPrintings.reverse
      val initialGameObjectState = emptyGameObjectState(Seq(p1, p2))
        .setLibrary(p1, p1Cards)
        .setLibrary(p2, p2Cards)

      val manager = createGameStateManager(gameData, initialGameObjectState, StartGameAction)
        .updateGameObjectState(s => s.setLibrary(p1, p1Cards.drop(7)).setHand(p1, p1Cards.take(7)).setLibrary(p2, p2Cards.drop(7)).setHand(p2, p2Cards.take(7)))
      val beforeMulliganGameObjectState = manager.gameState.gameObjectState
      manager.handleDecision("K", p1)
      manager.handleDecision("K", p2)

      manager.gameState.gameObjectState mustEqual beforeMulliganGameObjectState
      manager.nextAction mustEqual PriorityChoice(p1)
    }
  }
}
