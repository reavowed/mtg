package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.start.DrawStartingHandsAction
import mtg.game.{GameData, GameStartingData}

import scala.util.Random

case class GameState(
  gameData: GameData,
  gameObjectState: GameObjectState,
  gameHistory: GameHistory)
{
  def updateGameObjectState(newGameObjectState: GameObjectState): GameState = copy(gameObjectState = newGameObjectState)
  def recordEvents(events: Seq[GameEvent]): GameState = copy(gameHistory = gameHistory.addEvents(events))
}

object GameState {
  def initial(gameStartingData: GameStartingData) = {
    val startingPlayer = Random.shuffle(gameStartingData.players).head
    val playersInTurnOrder = GameData.getPlayersInApNapOrder(startingPlayer, gameStartingData.players)
    GameState(
      GameData(playersInTurnOrder),
      GameObjectState.initial(gameStartingData),
      GameHistory.empty)
  }
}
