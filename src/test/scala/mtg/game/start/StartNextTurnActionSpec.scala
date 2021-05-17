package mtg.game.start

import mtg.SpecWithGameObjectState
import mtg.game.{GameData, PlayerIdentifier}
import mtg.game.state.{GameAction, GameHistory, GameState}
import mtg.game.turns.{BeginTurnEvent, StartNextTurnAction}
import org.specs2.matcher.Matcher

class StartNextTurnActionSpec extends SpecWithGameObjectState {

  def beBeginTurnAction(player: PlayerIdentifier): Matcher[GameAction] = {
    beAnInstanceOf[BeginTurnEvent].and({ (action: GameAction) =>
      action.asInstanceOf[BeginTurnEvent].turn.activePlayer
    } ^^ beEqualTo(player))
  }

  "start next turn action" should {
    "pass turn from first to second player" in {
      val gameState = GameState(GameData.initial(Seq(playerOne, playerTwo)), emptyGameObjectState, GameHistory.empty, Nil)

      val (gameActions, _) = StartNextTurnAction(playerOne).execute(gameState)

      gameActions must contain(allOf(
        beBeginTurnAction(playerOne),
        StartNextTurnAction(playerTwo)
      ).inOrder)
    }

    "pass turn from second to first player" in {
      val gameState = GameState(GameData.initial(Seq(playerOne, playerTwo)), emptyGameObjectState, GameHistory.empty, Nil)

      val (gameActions, _) = StartNextTurnAction(playerTwo).execute(gameState)

      gameActions must contain(exactly(
        beBeginTurnAction(playerTwo),
        StartNextTurnAction(playerOne)
      ))
    }
  }
}
