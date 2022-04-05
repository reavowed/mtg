package mtg.stack.adding

import mtg.core.{ObjectId, PlayerId}
import mtg.game.state.history.LogEvent
import mtg.game.state.{CurrentCharacteristics, DelegatingGameAction, GameAction, GameState}

case class FinishCasting(playerId: PlayerId, stackObjectId: ObjectId) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    val stackObjectWithState = gameState.gameObjectState.derivedState.stackObjectStates(stackObjectId)
    LogEvent.CastSpell(
      playerId,
      CurrentCharacteristics.getName(stackObjectWithState),
      stackObjectWithState.gameObject.targets.map(CurrentCharacteristics.getName(_, gameState)))
  }
}
