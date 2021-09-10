package mtg.events

import mtg.effects.ContinuousEffect
import mtg.effects.condition.Condition
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}

case class CreateContinousEffects(effects: Seq[ContinuousEffect], endCondition: Condition) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.addEffects(effects, endCondition)
  }
}
