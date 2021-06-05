package mtg.abilities.builder

import mtg.effects.condition.ConditionDefinition
import mtg.effects.condition.event.{BeginningOfCombatConditionDefinition, EndOfTurnConditionDefinition}
import mtg.effects.identifiers.StaticIdentifier
import mtg.game.PlayerId

trait ConditionBuilder {
  def endOfTurn: ConditionDefinition = EndOfTurnConditionDefinition
  def beginningOfCombat(playerIdentifier: StaticIdentifier[PlayerId]): ConditionDefinition = BeginningOfCombatConditionDefinition(playerIdentifier)
}
