package mtg.abilities.builder

import mtg.core.{ObjectId, ObjectOrPlayerId}
import mtg.effects.filters.Filter
import mtg.effects.targets.{AnyTarget, TargetIdentifier}

trait TargetBuilder {
  def anyTarget: TargetIdentifier[ObjectOrPlayerId] = AnyTarget
  def target(filter: Filter[ObjectId]): TargetIdentifier[ObjectId] = new TargetIdentifier(filter)
}
