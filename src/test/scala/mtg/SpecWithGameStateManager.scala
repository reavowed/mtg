package mtg

import mtg.game.objects.{GameObject, GameObjectState}
import mtg.game.start.TakeTurnAction
import mtg.game.state._
import mtg.game.state.history.GameHistory
import mtg.game.turns.{Turn, TurnPhase}
import mtg.helpers.{GameStateManagerHelpers, GameUpdateHelpers, StackObjectHelpers}
import org.specs2.matcher.Matcher

import scala.collection.mutable

abstract class SpecWithGameStateManager
    extends SpecWithGameObjectState
    with GameStateManagerHelpers
    with GameUpdateHelpers
    with StackObjectHelpers
{
  case class RootWrapper(action: GameAction[Any]) extends RootGameAction {
    override def execute()(implicit gameState: GameState): NewGameActionResult.Partial[RootGameAction] = {
      NewGameActionResult.Delegated(action, (_: Any, _) => NewGameActionResult.GameOver(GameResult.Tie))
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
    new GameStateManager(gameState, _ => {}, mutable.Map(players.map(_ -> players.map(_ -> TurnPhase.AllPhasesAndSteps).toMap): _*))
  }

  def createGameStateManager(gameObjectState: GameObjectState, action: RootGameAction): GameStateManager = {
    createGameStateManager(createGameState(gameObjectState, action))
  }

  def createGameStateManager(gameObjectState: GameObjectState, action: GameAction[Any]): GameStateManager = {
    createGameStateManager(createGameState(gameObjectState, action))
  }

  def createGameStateManagerAtStartOfFirstTurn(gameObjectState: GameObjectState): GameStateManager = {
    createGameStateManager(gameObjectState, TakeTurnAction(Turn(1, playerOne)))
  }

  def runAction(action: OldGameUpdate, gameObjectState: GameObjectState): GameState = {
    runAction(WrappedOldUpdates(action), gameObjectState)
  }

  def runAction(action: GameAction[Any], gameObjectState: GameObjectState): GameState = {
    val initialGameState = createGameState(gameObjectState, action)
    GameActionExecutor.executeAllActions(initialGameState)
  }

  def runAction(action: RootGameAction, gameObjectState: GameObjectState): GameState = {
    val initialGameState = createGameState(gameObjectState, action)
    GameActionExecutor.executeAllActions(initialGameState)
  }
}
