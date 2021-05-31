package mtg.abilities.builder

import mtg.effects.filters.Filter
import mtg.effects.targets.{AnyTarget, ObjectOrPlayerTargetIdentifier, ObjectTargetIdentifier}
import mtg.game.ObjectId

trait TargetBuilder {
  def anyTarget: ObjectOrPlayerTargetIdentifier = AnyTarget
  def target(filter: Filter[ObjectId]): ObjectTargetIdentifier = new ObjectTargetIdentifier(filter)
}
