package mtg.abilities.builder

import mtg.effects.filters.Filter
import mtg.effects.targets.{AnyTarget, TargetIdentifier}
import mtg.game.{ObjectId, ObjectOrPlayer}

trait TargetBuilder {
  def anyTarget: TargetIdentifier[ObjectOrPlayer] = AnyTarget
  def target(filter: Filter[ObjectId]): TargetIdentifier[ObjectId] = new TargetIdentifier(filter)
}
