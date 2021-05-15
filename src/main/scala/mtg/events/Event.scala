package mtg.events

import mtg.game.GameData
import mtg.game.objects.GameObjectState
import mtg.game.state.GameEvent.ResolvedEvent
import mtg.game.state.{AutomaticGameAction, GameAction, GameState}

abstract class Event extends AutomaticGameAction {
  override def execute(currentGameState: GameState): (GameState, Seq[GameAction]) = {
    (execute(currentGameState.gameObjectState, currentGameState.gameData) match {
      case EventResult.UpdatedGameObjectState(newGameObjectState) =>
        (currentGameState.updateGameObjectState(newGameObjectState), Nil)
      case EventResult.SubEvents(newEvents) =>
        (currentGameState, newEvents)
      case EventResult.Nothing =>
        (currentGameState, Nil)
    }).mapLeft(_.recordEvent(ResolvedEvent(this)))
  }

  def execute(currentGameObjectState: GameObjectState, gameData: GameData): EventResult
}
