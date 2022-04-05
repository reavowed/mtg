package mtg

import mtg.actions.GameResultAction
import mtg.game.objects.{GameObject, GameObjectState}
import mtg.game.state._
import mtg.game.state.history.GameHistory
import mtg.game.turns.Turn
import mtg.game.turns.turnEvents.ExecuteTurn
import mtg.helpers.{GameStateManagerHelpers, GameUpdateHelpers, StackObjectHelpers}
import org.specs2.matcher.Matcher

abstract class SpecWithGameStateManager
    extends SpecWithGameObjectState
    with GameStateManagerHelpers
    with GameUpdateHelpers
    with StackObjectHelpers
{
  case class RootWrapper(action: GameAction[Any]) extends RootGameAction {
    override def delegate(implicit gameState: GameState): GameAction[RootGameAction] = {
      for {
        _ <- action
        result <- GameResultAction(GameResult.Tie)
      } yield result
    }
  }

  def beObjectWithState(gameObject: GameObject): Matcher[ObjectWithState] = {(objectWithState: ObjectWithState) =>
    (objectWithState.gameObject == gameObject, "", "")
  }

  def createGameState(gameObjectState: GameObjectState, action: RootGameAction): GameState = {
    GameState(gameData, gameObjectState, GameHistory.empty, Some(action), None)
  }

  def createGameState(gameObjectState: GameObjectState, action: GameAction[Any]): GameState = {
    createGameState(gameObjectState, RootWrapper(action))
  }

  def createGameStateManager(gameState: GameState): GameStateManager = {
    new GameStateManager(gameState, _ => {}, Stops.all(players))
  }

  def createGameStateManager(gameObjectState: GameObjectState, action: RootGameAction): GameStateManager = {
    createGameStateManager(createGameState(gameObjectState, action))
  }

  def createGameStateManager(gameObjectState: GameObjectState, action: GameAction[Any]): GameStateManager = {
    createGameStateManager(createGameState(gameObjectState, action))
  }

  def createGameStateManagerAtStartOfFirstTurn(gameObjectState: GameObjectState): GameStateManager = {
    createGameStateManager(gameObjectState, ExecuteTurn(Turn(1, playerOne)))
  }

  def runAction(action: GameObjectAction, gameObjectState: GameObjectState): GameState = {
    runAction(WrappedOldUpdates(action), gameObjectState)
  }

  def runAction(action: GameAction[Any], gameObjectState: GameObjectState): GameState = {
    val initialGameState = createGameState(gameObjectState, action)
    GameActionExecutor.executeAllActions(initialGameState)(Stops.all(players))
  }

  def runAction(action: RootGameAction, gameObjectState: GameObjectState): GameState = {
    val initialGameState = createGameState(gameObjectState, action)
    GameActionExecutor.executeAllActions(initialGameState)(Stops.all(players))
  }
}
