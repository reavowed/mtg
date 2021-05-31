package mtg.events

import mtg.effects.ContinuousEffect
import mtg.effects.condition.Condition
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class CreateContinousEffect(effect: ContinuousEffect, endCondition: Condition) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.addEffect(effect, endCondition)
  }
}
