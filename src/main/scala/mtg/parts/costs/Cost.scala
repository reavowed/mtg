package mtg.parts.costs

import mtg.game.state.{ObjectWithState, OldGameUpdate}

trait Cost {
  def text: String
  def isUnpayable(objectWithAbility: ObjectWithState): Boolean
  def payForAbility(objectWithAbility: ObjectWithState): Seq[OldGameUpdate]
}
