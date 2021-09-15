package mtg.game.start

import mtg.SpecWithGameObjectState
import mtg.game.state.history.GameHistory
import mtg.game.{GameData, PlayerId}
import mtg.game.state.{GameAction, GameState, GameActionResult}
import mtg.game.turns.StartNextTurnAction
import mtg.game.turns.turnEvents.BeginTurnEvent
import org.specs2.matcher.Matcher

class StartNextTurnActionSpec extends SpecWithGameObjectState {

  def beBeginTurnAction(player: PlayerId): Matcher[GameAction] = {
    beAnInstanceOf[BeginTurnEvent].and({ (action: GameAction) =>
      action.asInstanceOf[BeginTurnEvent].turn.activePlayer
    } ^^ beEqualTo(player))
  }

  "start next turn action" should {
    "pass turn from first to second player" in {
      val gameState = GameState(GameData.initial(Seq(playerOne, playerTwo)), emptyGameObjectState, GameHistory.empty, Nil)

      val result = StartNextTurnAction(playerOne).execute(gameState)

      result.childActions must contain(allOf(
        beBeginTurnAction(playerOne),
        StartNextTurnAction(playerTwo)
      ).inOrder)
    }

    "pass turn from second to first player" in {
      val gameState = GameState(GameData.initial(Seq(playerOne, playerTwo)), emptyGameObjectState, GameHistory.empty, Nil)

      val result = StartNextTurnAction(playerTwo).execute(gameState)

      result.childActions must contain(exactly(
        beBeginTurnAction(playerTwo),
        StartNextTurnAction(playerOne)
      ))
    }
  }
}
