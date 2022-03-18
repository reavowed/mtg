package mtg.abilities.builder

import mtg.core.PlayerId
import mtg.effects.condition.Condition
import mtg.effects.condition.event.{BeginningOfCombatCondition, EndOfTurnCondition}
import mtg.effects.identifiers.StaticIdentifier

trait ConditionBuilder {
  def endOfTurn: Condition = EndOfTurnCondition
  def beginningOfCombat(playerIdentifier: StaticIdentifier[PlayerId]): Condition = BeginningOfCombatCondition(playerIdentifier)
}
