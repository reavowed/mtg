package mtg.game.state

import mtg.events.{Event, EventResult}
import mtg.game.GameData
import mtg.game.objects.GameObjectState

import scala.annotation.tailrec

case class HandleEventsAction(events: Seq[Event], nextTransition: Transition) extends AutomaticGameAction {
  def execute(currentGameState: GameState): GameState = {
    val (newGameObjectState, resolvedEvents) = resolveEvents(events, Nil, currentGameState.gameObjectState, currentGameState.gameData)
    currentGameState
      .updateGameObjectState(newGameObjectState)
      .updateTransition(nextTransition)
      .recordEvents(resolvedEvents.map(GameEvent.ResolvedEvent))
  }

  @tailrec
  private def resolveEvents(eventsToResolve: Seq[Event], resolvedEvents: Seq[Event], currentGameObjectState: GameObjectState, gameData: GameData): (GameObjectState, Seq[Event]) = {
    eventsToResolve match {
      case nextEvent +: remainingEvents =>
        nextEvent.execute(currentGameObjectState, gameData) match {
          case EventResult.UpdatedGameObjectState(newGameObjectState) =>
            resolveEvents(remainingEvents, resolvedEvents :+ nextEvent, newGameObjectState, gameData)
          case EventResult.SubEvents(newEvents) =>
            resolveEvents(newEvents ++ remainingEvents, resolvedEvents :+ nextEvent, currentGameObjectState, gameData)
          case EventResult.Nothing =>
            resolveEvents(remainingEvents, resolvedEvents :+ nextEvent, currentGameObjectState, gameData)
        }
      case Nil =>
        (currentGameObjectState, resolvedEvents)
    }
  }
}
