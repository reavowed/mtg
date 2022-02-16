package mtg.effects.filters.base

import mtg.effects.EffectContext
import mtg.effects.filters.PartialFilter
import mtg.game.ObjectId
import mtg.game.state.{Characteristics, GameState}

trait CharacteristicFilter extends PartialFilter[ObjectId] {
  def matches(characteristics: Characteristics, gameState: GameState): Boolean
  override def matches(objectId: ObjectId, effectContext: EffectContext, gameState: GameState): Boolean = {
    gameState.gameObjectState.derivedState.allObjectStates.get(objectId).map(_.characteristics).exists(matches(_, gameState))
  }
}
