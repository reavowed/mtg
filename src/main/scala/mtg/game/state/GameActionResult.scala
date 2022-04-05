package mtg.game.state

import mtg.game.objects.GameObjectState
import mtg.game.state.history.LogEvent

sealed trait GameActionResult

object GameActionResult {
  case class NewGameObjectState(gameObjectState: GameObjectState) extends GameActionResult
  case class MoreActions(nextActions: Seq[InternalGameAction]) extends GameActionResult
  case class Result(gameResult: GameResult) extends GameActionResult
  case object Nothing extends GameActionResult

  implicit def updatedGameObjectState(gameObjectState: GameObjectState): GameActionResult = NewGameObjectState(gameObjectState)
  implicit def optionalUpdatedGameObjectState(gameObjectStateOption: Option[GameObjectState]): GameActionResult = gameObjectStateOption.map(NewGameObjectState).getOrElse(Nothing)
  implicit def nextAction(childAction: InternalGameAction): GameActionResult = MoreActions(Seq(childAction))
  implicit def optionalNextAction(actionOption: Option[InternalGameAction]): GameActionResult = actionOption.map(nextAction).getOrElse(Nothing)
  implicit def nextActions(childActions: Seq[InternalGameAction]): GameActionResult = MoreActions(childActions)
  implicit def nothing(unit: Unit): GameActionResult = Nothing
}
