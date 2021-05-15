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
  nextTransition: Transition)
{
  def updateGameObjectState(newGameObjectState: GameObjectState): GameState = copy(gameObjectState = newGameObjectState)
  def updateTransition(newTransition: Transition): GameState = copy(nextTransition = newTransition)
  def runEvents(events: Seq[Event], nextTransition: Transition): GameState = updateTransition(HandleEventsAction(events, nextTransition))
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
