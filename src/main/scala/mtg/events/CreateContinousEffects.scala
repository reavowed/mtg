package mtg.events

import mtg.effects.ContinuousEffect
import mtg.effects.condition.Condition
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class CreateContinousEffects(effects: Seq[ContinuousEffect], endCondition: Condition) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    currentGameState.gameObjectState.addEffects(effects, endCondition)
  }
  override def canBeReverted: Boolean = true
}
