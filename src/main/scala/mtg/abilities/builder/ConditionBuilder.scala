package mtg.abilities.builder

import mtg.effects.condition.ConditionDefinition
import mtg.effects.condition.event.EndOfTurnConditionDefinition

trait ConditionBuilder {
  def endOfTurn: ConditionDefinition = EndOfTurnConditionDefinition
}
