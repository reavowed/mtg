package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.start.StartGameAction
import mtg.game.state.history.HistoryEvent.{ResolvedAction, ResolvedChoice}
import mtg.game.state.history._
import mtg.game.turns.turnEvents.{BeginPhaseEvent, BeginStepEvent, BeginTurnEvent}
import mtg.game.turns.{Turn, TurnPhase, TurnStep}
import mtg.game.{GameData, GameStartingData, PlayerId}

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
  def currentTurn: Option[Turn] = gameHistory.historyEvents.actions.collectFirst {
    case BeginTurnEvent(turn) => turn
  }
  def currentPhase: Option[TurnPhase] =  gameHistory.historyEvents.actions.collectFirst {
    case BeginTurnEvent(_) => None
    case BeginPhaseEvent(phase) => Some(phase)
  }.flatten
  def currentStep: Option[TurnStep] = gameHistory.historyEvents.actions.collectFirst {
    case BeginTurnEvent(_) => None
    case BeginPhaseEvent(_) => None
    case BeginStepEvent(step) => Some(step)
  }.flatten

  def handleActionResult(actionResult: GameActionResult): GameState = {
    addActions(actionResult.childActions).recordLogEvent(actionResult.logEvent)
  }

  def updateHistory(f: GameHistory => GameHistory): GameState = copy(gameHistory = f(gameHistory))
  def updateGameObjectState(f: GameObjectState => GameObjectState): GameState = updateGameObjectState(f(gameObjectState))
  def updateGameObjectState(newGameObjectState: Option[GameObjectState]): GameState = newGameObjectState.map(updateGameObjectState).getOrElse(this)
  def updateGameObjectState(newGameObjectState: GameObjectState): GameState = copy(gameObjectState = newGameObjectState)
  def recordAction(action: InternalGameAction): GameState = recordHistoryEvent(ResolvedAction(action, this))
  def recordChoice(choice: Choice): GameState = recordHistoryEvent(ResolvedChoice(choice, this))
  def recordHistoryEvent(event: HistoryEvent): GameState = copy(gameHistory = gameHistory.addGameEvent(event))
  def recordLogEvent(event: LogEvent): GameState = copy(gameHistory = gameHistory.addLogEvent(event))
  def recordLogEvent(event: Option[LogEvent]): GameState = event.map(recordLogEvent).getOrElse(this)
  private def setActions(newActions: Seq[GameAction]) = copy(pendingActions = newActions)
  def addActions(newActions: Seq[GameAction]) = setActions(newActions ++ pendingActions)
  def popAction(): (GameAction, GameState) = (pendingActions.head, setActions(pendingActions.tail))

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
