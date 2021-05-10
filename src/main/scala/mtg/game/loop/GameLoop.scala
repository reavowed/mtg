package mtg.game.loop

import mtg.events.Event
import mtg.game.GameState

import scala.annotation.tailrec

object GameLoop {
  @tailrec
  def executeAction(action: Action): Either[Choice, GameResult] = {
    action.execute() match {
      case Left(newEvent) =>
        executeAction(newEvent)
      case Right(choiceOrResult) =>
        choiceOrResult
    }
  }

  def resolveEvent(event: Event, gameState: GameState): GameState = {
    resolveEvents(Seq(event), gameState)
  }

  @tailrec
  private def resolveEvents(events: Seq[Event], gameState: GameState): GameState = {
    events match {
      case nextEvent +: remainingEvents =>
        nextEvent.execute(gameState) match {
          case Left(newGameState) =>
            resolveEvents(remainingEvents, newGameState)
          case Right(newEvents) =>
            resolveEvents(newEvents ++ remainingEvents, gameState)
        }
      case Nil =>
        gameState
    }
  }
}
