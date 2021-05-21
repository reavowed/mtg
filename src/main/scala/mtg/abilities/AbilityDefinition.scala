package mtg.abilities

import mtg.effects.Effect
import mtg.parts.costs.Cost

sealed class AbilityDefinition

case class ActivatedAbilityDefinition(
    costs: Seq[Cost],
    effects: Seq[Effect])
  extends AbilityDefinition
