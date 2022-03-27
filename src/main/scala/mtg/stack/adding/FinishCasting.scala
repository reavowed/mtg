package mtg.stack.adding

import mtg.core.{ObjectId, PlayerId}
import mtg.game.state.history.LogEvent
import mtg.game.state.{CurrentCharacteristics, ExecutableGameAction, GameState, PartialGameActionResult}

case class FinishCasting(playerId: PlayerId, stackObjectId: ObjectId) extends ExecutableGameAction[Any] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Any] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObjectId)
    PartialGameActionResult.child(
      LogEvent.CastSpell(
        playerId,
        CurrentCharacteristics.getName(stackObjectWithState),
        stackObjectWithState.gameObject.targets.map(CurrentCharacteristics.getName(_, gameState))))
  }
}
