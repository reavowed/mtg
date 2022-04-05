package mtg.stack.adding

import mtg.core.ObjectId
import mtg.game.state.{DelegatingGameAction, GameAction, GameState}

object GetMostRecentStackObjectId extends DelegatingGameAction[ObjectId] {
  override def delegate(implicit gameState: GameState): GameAction[ObjectId] = {
    gameState.gameObjectState.stack.last.objectId
  }
}
