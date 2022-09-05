package mtg.game.state

import mtg.core.PlayerId
import mtg.game.objects.GameObjectState
import mtg.game.start.StartGameAction
import mtg.game.state.history.HistoryEvent.ResolvedChoice
import mtg.game.state.history._
import mtg.game.turns.turnEvents.{ExecutePhase, ExecuteStep, ExecuteTurn}
import mtg.game.turns.{Turn, TurnPhase, TurnStep}
import mtg.game.{GameData, GameStartingData}

import scala.annotation.tailrec

case class GameState(
  gameData: GameData,
  gameObjectState: GameObjectState,
  gameHistory: GameHistory,
  currentActionExecutionState: GameActionExecutionState.Halting[RootGameAction])
{
  def activePlayer: PlayerId = currentTurn.get.activePlayer
  def playersInApnapOrder: Seq[PlayerId] = gameData.getPlayersInApNapOrder(activePlayer)

  def allCurrentActions: Seq[GameAction[_]] = {
    @tailrec
    def helper(currentState: GameActionExecutionState.Halting[_], actions: Seq[GameAction[_]]): Seq[GameAction[_]] = {
      currentState match {
        case GameActionExecutionState.Action(gameAction) => actions :+ gameAction
        case GameActionExecutionState.DelegatingAction(gameAction, innerExecutionState, _) => helper(innerExecutionState, actions :+ gameAction)
        case GameActionExecutionState.FlatMapped(innerExecutionState, _) => helper(innerExecutionState, actions)
        case GameActionExecutionState.Result(_) => actions
      }
    }
    helper(currentActionExecutionState, Nil)
  }

  def currentTurn: Option[Turn] = allCurrentActions.headOption.flatMap(_.asOptionalInstanceOf[ExecuteTurn]).map(_.turn)
  def currentTurnNumber: Int = currentTurn.map(_.number).getOrElse(0)
  def currentPhase: Option[TurnPhase] = allCurrentActions.ofType[ExecutePhase].headOption.map(_.phase)
  def currentStep: Option[TurnStep] = allCurrentActions.ofType[ExecuteStep].headOption.map(_.step)

  def updateHistory(f: GameHistory => GameHistory): GameState = copy(gameHistory = f(gameHistory))
  def updateGameObjectState(f: GameObjectState => GameObjectState): GameState = updateGameObjectState(f(gameObjectState))
  def updateGameObjectState(newGameObjectState: Option[GameObjectState]): GameState = newGameObjectState.map(updateGameObjectState).getOrElse(this)
  def updateGameObjectState(newGameObjectState: GameObjectState): GameState = copy(gameObjectState = newGameObjectState)
  def recordAction(event: HistoryEvent.ResolvedAction[_]): GameState = recordHistoryEvent(event)
  def recordChoice[DecisionType](choice: Choice[DecisionType], decision: DecisionType): GameState = recordHistoryEvent(ResolvedChoice(choice, decision, this))
  def recordHistoryEvent(event: HistoryEvent): GameState = copy(gameHistory = gameHistory.addGameEvent(event))
  def recordLogEvent(event: LogEvent): GameState = copy(gameHistory = gameHistory.addLogEvent(event))
  def recordLogEvent(event: Option[LogEvent]): GameState = event.map(recordLogEvent).getOrElse(this)

  def updateActionExecutionState(newActionExecutionState: GameActionExecutionState.Halting[RootGameAction]): GameState = {
    copy(currentActionExecutionState = newActionExecutionState)
  }

  override def toString: String = "GameState"
}

object GameState {
  def initial(gameStartingData: GameStartingData): GameState = {
    val startingPlayer = gameStartingData.players.head
    val playersInTurnOrder = GameData.getPlayersInApNapOrder(startingPlayer, gameStartingData.players)
    val gameData = GameData.initial(playersInTurnOrder)
    GameState(
      gameData,
      GameObjectState.initial(gameStartingData, gameData),
      GameHistory.empty,
      GameActionExecutionState.Action(StartGameAction))
  }
}
