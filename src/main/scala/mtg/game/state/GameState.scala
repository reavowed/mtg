package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.start.StartGameAction
import mtg.game.state.history._
import mtg.game.turns.{Turn, TurnPhase, TurnStep}
import mtg.game.{GameData, GameStartingData, PlayerIdentifier}

import scala.util.Random

case class GameState(
  gameData: GameData,
  gameObjectState: GameObjectState,
  gameHistory: GameHistory,
  pendingActions: Seq[GameAction])
{
  def activePlayer: PlayerIdentifier = gameHistory.turns.last.turn.activePlayer
  def playersInApnapOrder: Seq[PlayerIdentifier] = gameData.getPlayersInApNapOrder(activePlayer)

  def currentTurnNumber: Int = gameHistory.turns.length
  private def currentTurnHistory: Option[TurnHistory] = gameHistory.turns.lastOption
  def currentTurn: Option[Turn] = gameHistory.forCurrentTurn.map(_.turn)
  private def currentPhaseHistory: Option[PhaseHistory] = currentTurnHistory.flatMap(_.phases.lastOption)
  def currentPhase: Option[TurnPhase] = currentPhaseHistory.map(_.phase)
  private def currentStepHistory: Option[StepHistory] = currentPhaseHistory.flatMap(_.asOptionalInstanceOf[PhaseHistoryWithSteps]).flatMap(_.steps.lastOption)
  def currentStep: Option[TurnStep] = currentStepHistory.map(_.step)

  def updateHistory(f: GameHistory => GameHistory): GameState = copy(gameHistory = f(gameHistory))
  def updateGameObjectState(newGameObjectState: GameObjectState): GameState = copy(gameObjectState = newGameObjectState)
  def recordGameEvent(event: GameEvent): GameState = copy(gameHistory = gameHistory.addGameEvent(event))
  def recordLogEvent(event: LogEvent): GameState = copy(gameHistory = gameHistory.addLogEvent(event))
  def recordLogEvent(event: Option[LogEvent]): GameState = event.map(recordLogEvent).getOrElse(this)
  private def setActions(newActions: Seq[GameAction]) = copy(pendingActions = newActions)
  def addActions(newActions: Seq[GameAction]) = setActions(newActions ++ pendingActions)
  def popAction(): (GameAction, GameState) = (pendingActions.head, setActions(pendingActions.tail))
}

object GameState {
  def initial(gameStartingData: GameStartingData) = {
    val startingPlayer = Random.shuffle(gameStartingData.players).head
    val playersInTurnOrder = GameData.getPlayersInApNapOrder(startingPlayer, gameStartingData.players)
    GameState(
      GameData.initial(playersInTurnOrder),
      GameObjectState.initial(gameStartingData),
      GameHistory.empty,
      Seq(StartGameAction))
  }
}
