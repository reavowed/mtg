package mtg.game.state

import mtg.core.PlayerId
import mtg.game.objects.GameObjectState
import mtg.game.start.StartGameAction
import mtg.game.state.history.HistoryEvent.{ResolvedAction, ResolvedChoice}
import mtg.game.state.history._
import mtg.game.turns.turnEvents.{ExecutePhase, ExecuteStep, ExecuteTurn}
import mtg.game.turns.{Turn, TurnPhase, TurnStep}
import mtg.game.{GameData, GameStartingData}

case class GameState(
  gameData: GameData,
  gameObjectState: GameObjectState,
  gameHistory: GameHistory,
  currentAction: Option[GameAction[RootGameAction]],
  result: Option[GameResult])
{
  def activePlayer: PlayerId = currentTurn.get.activePlayer
  def playersInApnapOrder: Seq[PlayerId] = gameData.getPlayersInApNapOrder(activePlayer)

  def allCurrentActions: Seq[GameAction[_]] = {
    def helper(current: GameAction[_], actions: Seq[GameAction[_]]): Seq[GameAction[_]] = {
      current match {
        case PartiallyExecutedActionWithDelegate(rootAction, childAction) =>
          helper(childAction, actions :+ rootAction)
        case PartiallyExecutedActionWithFlatMap(rootAction, childAction, f) =>
          helper(childAction, actions :+ rootAction)
        case _ =>
          actions :+ current
      }
    }
    currentAction.toSeq.flatMap(helper(_, Nil))
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

  def updateAction(action: GameAction[RootGameAction]): GameState = copy(currentAction = Some(action))

  override def toString: String = "GameState"
}

object GameState {
  def initial(gameStartingData: GameStartingData) = {
    val startingPlayer = gameStartingData.players.head
    val playersInTurnOrder = GameData.getPlayersInApNapOrder(startingPlayer, gameStartingData.players)
    val gameData = GameData.initial(playersInTurnOrder)
    GameState(
      gameData,
      GameObjectState.initial(gameStartingData, gameData),
      GameHistory.empty,
      Some(StartGameAction),
      None)
  }
}
