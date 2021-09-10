package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.start.StartGameAction
import mtg.game.state.history.GameEvent.ResolvedEvent
import mtg.game.state.history._
import mtg.game.{GameData, GameStartingData, PlayerId}

import scala.reflect.{ClassTag, classTag}
import scala.util.Random

case class GameState(
  gameData: GameData,
  gameObjectState: GameObjectState,
  turnState: TurnState,
  gameHistory: GameHistory,
  pendingActions: Seq[GameAction])
{
  def activePlayer: PlayerId = turnState.currentTurn.get.activePlayer
  def playersInApnapOrder: Seq[PlayerId] = gameData.getPlayersInApNapOrder(activePlayer)

  def handleActionResult(actionResult: GameActionResult): GameState = {
    addActions(actionResult.childActions).recordLogEvent(actionResult.logEvent)
  }

  def updateGameObjectState(f: GameObjectState => GameObjectState): GameState = updateGameObjectState(f(gameObjectState))
  def updateGameObjectState(newGameObjectState: GameObjectState): GameState = copy(gameObjectState = newGameObjectState)
  def recordGameEvent(event: GameObjectEvent): GameState = recordGameEvent(ResolvedEvent(event, gameObjectState.derivedState))
  def recordGameEvent(event: GameEvent): GameState = copy(gameHistory = gameHistory.addGameEvent(event, this))
  def recordLogEvent(event: LogEvent): GameState = copy(gameHistory = gameHistory.addLogEvent(event))
  def recordLogEvent(event: Option[LogEvent]): GameState = event.map(recordLogEvent).getOrElse(this)
  private def setActions(newActions: Seq[GameAction]) = copy(pendingActions = newActions)
  def addActions(newActions: Seq[GameAction]) = setActions(newActions ++ pendingActions)
  def popAction(): (GameAction, GameState) = (pendingActions.head, setActions(pendingActions.tail))

  def eventsThisTurn: Iterable[GameEvent] = gameHistory.recentEventsWhile(_.stateBefore.turnState.currentTurn == turnState.currentTurn).map(_.gameEvent)
  def eventsSinceEvent[T <: GameObjectEvent : ClassTag]: Iterable[GameEvent] = gameHistory.recentEventsWhile {
      case GameEventWithPreviousState(ResolvedEvent(e, _), _) if classTag[T].runtimeClass.isInstance(e) => false
      case _ => true
  }.map(_.gameEvent)

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
      TurnState.initial,
      GameHistory.empty,
      Seq(StartGameAction))
  }
}
