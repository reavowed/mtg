package mtg.abilities

import mtg.effects.InstructionResolutionContext
import mtg.game.objects.AbilityOnTheStack

case class PendingTriggeredAbility(id: Int, triggeredAbility: TriggeredAbility, resolutionContext: InstructionResolutionContext) {
  def toAbilityOnTheStack: AbilityOnTheStack = AbilityOnTheStack(triggeredAbility.definition, triggeredAbility.sourceId, triggeredAbility.controllerId, resolutionContext.identifiedObjects)
}
