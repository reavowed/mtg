package mtg.parts.costs

import mtg.game.state.{GameAction, ObjectWithState}

trait Cost {
  def text: String
  def payForAbility(abilitySource: ObjectWithState): Seq[GameAction]
}
