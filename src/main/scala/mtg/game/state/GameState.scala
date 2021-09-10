package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.start.StartGameAction
import mtg.game.state.history.GameEvent.StateChange
import mtg.game.state.history._
import mtg.game.turns.{Turn, TurnPhase, TurnStep}
import mtg.game.turns.turnEvents.{BeginPhaseEvent, BeginStepEvent, BeginTurnEvent}
import mtg.game.{GameData, GameStartingData, PlayerId}

import scala.reflect.{ClassTag, classTag}
import scala.util.Random

case class GameState(
  gameData: GameData,
  gameObjectState: GameObjectState,
  gameHistory: GameHistory,
  pendingActions: Seq[GameAction])
{
  def activePlayer: PlayerId = currentTurn.get.activePlayer
  def playersInApnapOrder: Seq[PlayerId] = gameData.getPlayersInApNapOrder(activePlayer)

  def currentTurnNumber: Int = currentTurn.map(_.number).getOrElse(0)
  def currentTurn: Option[Turn] = gameHistory.gameEventsWithPreviousStates.iterator.map(_.gameEvent).ofType[BeginTurnEvent].headOption.map(_.turn)
  def currentPhase: Option[TurnPhase] = gameHistory.gameEventsWithPreviousStates.iterator.map(_.gameEvent).ofType[BeginPhaseEvent].headOption.map(_.phase)
  def currentStep: Option[TurnStep] = gameHistory.gameEventsWithPreviousStates.iterator.map(_.gameEvent).ofType[BeginStepEvent].headOption.map(_.step)

  def updateGameObjectState(f: GameObjectState => GameObjectState): GameState = updateGameObjectState(f(gameObjectState))
  def updateGameObjectState(newGameObjectState: Option[GameObjectState]): GameState = newGameObjectState.map(updateGameObjectState).getOrElse(this)
  def updateGameObjectState(newGameObjectState: GameObjectState): GameState = recordGameEvent(StateChange(gameObjectState.derivedState)).copy(gameObjectState = newGameObjectState)
  def recordGameEvent(eventOption: Option[GameEvent]): GameState = eventOption.map(recordGameEvent).getOrElse(this)
  def recordGameEvent(event: GameEvent): GameState = copy(gameHistory = gameHistory.addGameEvent(event, this))
  def recordLogEvent(event: LogEvent): GameState = copy(gameHistory = gameHistory.addLogEvent(event))
  def recordLogEvent(event: Option[LogEvent]): GameState = event.map(recordLogEvent).getOrElse(this)
  private def setActions(newActions: Seq[GameAction]) = copy(pendingActions = newActions)
  def addActions(newActions: Seq[GameAction]) = setActions(newActions ++ pendingActions)
  def popAction(): (GameAction, GameState) = (pendingActions.head, setActions(pendingActions.tail))

  def eventsThisTurn: Iterable[GameEvent] = gameHistory.gameEventsWithPreviousStates.view.takeWhile(e => !e.gameEvent.isInstanceOf[BeginTurnEvent]).map(_.gameEvent)
  def eventsSinceEvent[T <: GameEvent : ClassTag]: Iterable[GameEvent] = gameHistory.gameEventsWithPreviousStates.view.map(_.gameEvent).takeWhile(!classTag[T].runtimeClass.isInstance(_))

  override def toString: String = "GameState"
}

object GameState {
  def initial(gameStartingData: GameStartingData) = {
    val startingPlayer = Random.shuffle(gameStartingData.players).head
    val playersInTurnOrder = GameData.getPlayersInApNapOrder(startingPlayer, gameStartingData.players)
    val gameData = GameData.initial(playersInTurnOrder)
    GameState(
      gameData,
      GameObjectState.initial(gameStartingData, gameData),
      GameHistory.empty,
      Seq(StartGameAction))
  }
}
