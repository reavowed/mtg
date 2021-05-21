package mtg.parts.costs

import mtg.game.state.{GameAction, ObjectWithState}

trait Cost {
  def text: String
  def isUnpayable(abilitySource: ObjectWithState): Boolean
  def payForAbility(abilitySource: ObjectWithState): Seq[GameAction]
}
