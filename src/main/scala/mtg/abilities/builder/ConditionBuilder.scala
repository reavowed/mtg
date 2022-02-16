package mtg.abilities.builder

import mtg.core.PlayerId
import mtg.effects.condition.ConditionDefinition
import mtg.effects.condition.event.{BeginningOfCombatConditionDefinition, EndOfTurnConditionDefinition}
import mtg.effects.identifiers.StaticIdentifier

trait ConditionBuilder {
  def endOfTurn: ConditionDefinition = EndOfTurnConditionDefinition
  def beginningOfCombat(playerIdentifier: StaticIdentifier[PlayerId]): ConditionDefinition = BeginningOfCombatConditionDefinition(playerIdentifier)
}
