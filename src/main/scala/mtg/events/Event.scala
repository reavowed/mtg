package mtg.events

import mtg.game.GameData
import mtg.game.objects.GameObjectState

abstract class Event {
  def execute(currentGameObjectState: GameObjectState, gameData: GameData): EventResult
}
