package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.start.InitialDrawAction
import mtg.game.{GameData, GameStartingData}

import scala.util.Random

case class GameState(
  gameData: GameData,
  gameObjectState: GameObjectState,
  nextTransition: Transition)

object GameState {
  def initial(gameStartingData: GameStartingData) = {
    val startingPlayer = Random.shuffle(gameStartingData.players).head
    GameState(
      GameData(GameData.getPlayersInApNapOrder(startingPlayer, gameStartingData.players)),
      GameObjectState.initial(gameStartingData),
      InitialDrawAction)
  }
}
