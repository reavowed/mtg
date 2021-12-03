package mtg.undo

import mtg.SpecWithGameStateManager
import mtg.data.cards.Plains
import mtg.game.PlayerId
import mtg.game.state.{GameStateManager, UndoHelper}
import mtg.game.turns.{StartNextTurnAction, TurnPhase}

class UndoSpec extends SpecWithGameStateManager {
  def verifyUndo(
    manager: GameStateManager,
    action: GameStateManager => Unit,
    player: PlayerId,
    shouldUndoWork: Boolean
  ) = {
    val stateBeforeAction = manager.gameState
    action(manager)
    val stateAfterAction = manager.gameState

    UndoHelper.canUndo(player, manager.gameState) mustEqual shouldUndoWork

    manager.requestUndo(player)
    manager.gameState mustEqual (if (shouldUndoWork) stateBeforeAction else stateAfterAction)
  }

  "undoing" should {
    "revert tapping a basic land for mana" in {
      val initialState = emptyGameObjectState.setBattlefield(playerOne, Plains)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)

      verifyUndo(manager, _.activateAbility(playerOne, Plains), playerOne, true)
    }

    "not revert tapping a basic land for mana if requested by the wrong player" in {
      val initialState = emptyGameObjectState.setBattlefield(playerOne, Plains)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)

      verifyUndo(manager, _.activateAbility(playerOne, Plains), playerTwo, false)
    }

    "not revert a priority pass" in {
      val initialState = emptyGameObjectState

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)

      verifyUndo(manager, _.passPriority(playerOne), playerOne, false)
    }
  }
}
