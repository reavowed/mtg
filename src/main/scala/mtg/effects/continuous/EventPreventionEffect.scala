package mtg.effects.continuous

import mtg.effects.ContinuousEffect
import mtg.game.state.{GameObjectAction, GameState}

trait EventPreventionEffect extends ContinuousEffect {
  def preventsEvent(gameObjectEvent: GameObjectAction, gameState: GameState): Boolean
}
