package mtg.game.state

import mtg.events.Event
import mtg.game.objects.GameObjectState
import mtg.game.start.DrawStartingHandsAction
import mtg.game.{GameData, GameStartingData}

import scala.util.Random

case class GameState(
  gameData: GameData,
  gameObjectState: GameObjectState,
  gameHistory: GameHistory,
  nextAction: GameAction)
{
  def updateGameObjectState(newGameObjectState: GameObjectState): GameState = copy(gameObjectState = newGameObjectState)
  def updateAction(newAction: GameAction): GameState = copy(nextAction = newAction)
  def runEvents(events: Seq[Event], nextAction: GameAction): GameState = updateAction(HandleEventsAction(events, nextAction))
  def recordEvents(events: Seq[GameEvent]): GameState = copy(gameHistory = gameHistory.addEvents(events))
}

object GameState {
  def initial(gameStartingData: GameStartingData) = {
    val startingPlayer = Random.shuffle(gameStartingData.players).head
    val playersInTurnOrder = GameData.getPlayersInApNapOrder(startingPlayer, gameStartingData.players)
    GameState(
      GameData(playersInTurnOrder),
      GameObjectState.initial(gameStartingData),
      GameHistory.empty,
      DrawStartingHandsAction(playersInTurnOrder, 0))
  }
}
