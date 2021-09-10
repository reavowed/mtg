package mtg.events

import mtg.effects.ContinuousObjectEffect
import mtg.effects.condition.Condition
import mtg.game.state.{GameObjectAction, GameObjectActionResult, GameState}

case class CreateContinousEffects(effects: Seq[ContinuousObjectEffect], endCondition: Condition) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    currentGameState.gameObjectState.addEffects(effects, endCondition)
  }
}
