package mtg.game.state

import mtg.events.{Event, EventResult}
import mtg.game.GameData
import mtg.game.objects.GameObjectState

import scala.annotation.tailrec

case class HandleEventsAction(events: Seq[Event], nextTransition: Transition) extends Action {
  def runAction(currentGameObjectState: GameObjectState, gameData: GameData): (GameObjectState, Transition) = {
    (resolveEvents(events, currentGameObjectState, gameData), nextTransition)
  }

  @tailrec
  private def resolveEvents(events: Seq[Event], gameObjectState: GameObjectState, gameData: GameData): GameObjectState = {
    events match {
      case nextEvent +: remainingEvents =>
        nextEvent.execute(gameObjectState, gameData) match {
          case EventResult.UpdatedGameObjectState(newGameObjectState) =>
            resolveEvents(remainingEvents, newGameObjectState, gameData)
          case EventResult.SubEvents(newEvents) =>
            resolveEvents(newEvents ++ remainingEvents, gameObjectState, gameData)
          case EventResult.Nothing =>
            resolveEvents(remainingEvents, gameObjectState, gameData)
        }
      case Nil =>
        gameObjectState
    }
  }
}
