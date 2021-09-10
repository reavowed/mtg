package mtg.effects.continuous

import mtg.effects.ContinuousObjectEffect
import mtg.game.state.{GameObjectAction, GameState}

trait GameObjectActionPreventionEffect extends ContinuousObjectEffect {
  def preventsEvent(gameObjectEvent: GameObjectAction, gameState: GameState): Boolean
}
