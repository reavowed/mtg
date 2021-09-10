package mtg.game.start

import mtg.SpecWithGameObjectState
import mtg.game.state.history.GameHistory
import mtg.game.{GameData, PlayerId}
import mtg.game.state.{GameAction, GameState, InternalGameActionResult}
import mtg.game.turns.StartNextTurnAction
import mtg.game.turns.turnEvents.BeginTurnAction
import org.specs2.matcher.Matcher

class StartNextTurnActionSpec extends SpecWithGameObjectState {

  def beBeginTurnAction(player: PlayerId): Matcher[GameAction] = {
    beAnInstanceOf[BeginTurnAction].and({ (action: GameAction) =>
      action.asInstanceOf[BeginTurnAction].turn.activePlayer
    } ^^ beEqualTo(player))
  }

  "start next turn action" should {
    "pass turn from first to second player" in {
      val gameState = createGameState(emptyGameObjectState, Nil)

      val InternalGameActionResult(gameActions, _, _) = StartNextTurnAction(playerOne).execute(gameState)

      gameActions must contain(allOf(
        beBeginTurnAction(playerOne),
        StartNextTurnAction(playerTwo)
      ).inOrder)
    }

    "pass turn from second to first player" in {
      val gameState = createGameState(emptyGameObjectState, Nil)

      val InternalGameActionResult(gameActions, _, _) = StartNextTurnAction(playerTwo).execute(gameState)

      gameActions must contain(exactly(
        beBeginTurnAction(playerTwo),
        StartNextTurnAction(playerOne)
      ))
    }
  }
}
