package mtg

import mtg.cards.{CardDefinition, CardPrinting}
import mtg.game.PlayerIdentifier
import mtg.game.objects.{Card, CardObject, GameObject, GameObjectState}
import mtg.game.state.history.GameHistory
import mtg.game.state.{GameAction, GameResult, GameState, GameStateManager, ObjectWithState}
import mtg.game.turns.TurnPhase
import mtg.game.turns.priority.PriorityChoice
import mtg.helpers.{GameActionHelpers, GameStateManagerHelpers}
import org.specs2.matcher.Matcher

import scala.collection.mutable

abstract class SpecWithGameStateManager
    extends SpecWithGameObjectState
    with GameStateManagerHelpers
    with GameActionHelpers
{
  def beCardObject(card: Card): Matcher[GameObject] = { (gameObject: GameObject) =>
    (gameObject.asOptionalInstanceOf[CardObject].exists(_.card == card), "", "")
  }
  def beCardObject(cardDefinition: CardDefinition): Matcher[GameObject] = { (gameObject: GameObject) =>
    (gameObject.asOptionalInstanceOf[CardObject].exists(_.card.printing.cardDefinition == cardDefinition), "", "")
  }
  def beObjectWithState(gameObject: GameObject): Matcher[ObjectWithState] = {(objectWithState: ObjectWithState) =>
    (objectWithState.gameObject == gameObject, "", "")
  }

  def createGameState(gameObjectState: GameObjectState, actions: Seq[GameAction]): GameState = {
    GameState(gameData, gameObjectState, GameHistory.empty, actions)
  }

  def createGameStateManager(gameObjectState: GameObjectState, actions: Seq[GameAction]): GameStateManager = {
    new GameStateManager(createGameState(gameObjectState, actions), _ => {}, mutable.Map(players.map(_ -> players.map(_ -> TurnPhase.AllPhasesAndSteps).toMap): _*))
  }

  def createGameStateManager(gameObjectState: GameObjectState, action: GameAction): GameStateManager = {
    createGameStateManager(gameObjectState, Seq(action, GameResult.Tie))
  }

  def runAction(action: GameAction, gameObjectState: GameObjectState): GameState = {
    createGameStateManager(gameObjectState, action).currentGameState
  }
}
