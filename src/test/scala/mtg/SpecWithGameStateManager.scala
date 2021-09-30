package mtg

import mtg.game.objects.{GameObject, GameObjectState}
import mtg.game.state._
import mtg.game.state.history.GameHistory
import mtg.game.turns.TurnPhase
import mtg.helpers.{GameUpdateHelpers, GameStateManagerHelpers, StackObjectHelpers}
import org.specs2.matcher.Matcher

import scala.collection.mutable

abstract class SpecWithGameStateManager
    extends SpecWithGameObjectState
    with GameStateManagerHelpers
    with GameUpdateHelpers
    with StackObjectHelpers
{
  def beObjectWithState(gameObject: GameObject): Matcher[ObjectWithState] = {(objectWithState: ObjectWithState) =>
    (objectWithState.gameObject == gameObject, "", "")
  }

  def createGameState(gameObjectState: GameObjectState, actions: Seq[GameUpdate]): GameState = {
    GameState(gameData, gameObjectState, GameHistory.empty, actions)
  }

  def createGameStateManager(gameObjectState: GameObjectState, actions: Seq[GameUpdate]): GameStateManager = {
    new GameStateManager(createGameState(gameObjectState, actions), _ => {}, mutable.Map(players.map(_ -> players.map(_ -> TurnPhase.AllPhasesAndSteps).toMap): _*))
  }

  def createGameStateManager(gameObjectState: GameObjectState, action: GameUpdate): GameStateManager = {
    createGameStateManager(gameObjectState, Seq(action, GameResult.Tie))
  }

  def runAction(action: GameUpdate, gameObjectState: GameObjectState): GameState = {
    createGameStateManager(gameObjectState, action).gameState
  }
}
