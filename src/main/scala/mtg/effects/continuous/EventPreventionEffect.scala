package mtg.effects.continuous

import mtg.game.state.{GameObjectEvent, GameState}

trait EventPreventionEffect extends ContinuousEffect {
  def preventsEvent(gameObjectEvent: GameObjectEvent, gameState: GameState): Boolean
}
