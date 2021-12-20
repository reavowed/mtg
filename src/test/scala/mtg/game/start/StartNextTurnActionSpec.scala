package mtg.game.start

import mtg.SpecWithGameStateManager
import mtg.game.PlayerId
import mtg.game.state.GameUpdate
import mtg.game.turns.Turn
import mtg.game.turns.turnEvents.{BeginTurnEvent, TakeTurnAction}
import org.specs2.matcher.Matcher

class StartNextTurnActionSpec extends SpecWithGameStateManager {

  def beBeginTurnAction(player: PlayerId): Matcher[GameUpdate] = {
    beAnInstanceOf[BeginTurnEvent].and({ (action: GameUpdate) =>
      action.asInstanceOf[BeginTurnEvent].turn.activePlayer
    } ^^ beEqualTo(player))
  }

  "start next turn action" should {
    "pass turn from first to second player" in {
      val manager = createGameStateManager(emptyGameObjectState, TakeTurnAction(Turn(1, playerOne)))

      manager.passUntilTurn(2)

      manager.gameState.currentTurn must beSome(Turn(2, playerTwo))
    }

    "pass turn from second to first player" in {
      val manager = createGameStateManager(emptyGameObjectState, TakeTurnAction(Turn(2, playerTwo)))

      manager.passUntilTurn(3)

      manager.gameState.currentTurn must beSome(Turn(3, playerOne))
    }
  }
}
