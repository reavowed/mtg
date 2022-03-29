package mtg.abilities.builder

import mtg.core.PlayerId
import mtg.effects.condition.Condition
import mtg.effects.condition.event.{BeginningOfCombatCondition, EndOfTurnCondition}
import mtg.instructions.nouns.StaticSingleIdentifyingNounPhrase

trait ConditionBuilder {
  def endOfTurn: Condition = EndOfTurnCondition
  def beginningOfCombat(playerPhrase: StaticSingleIdentifyingNounPhrase[PlayerId]): Condition = BeginningOfCombatCondition(playerPhrase)
}
