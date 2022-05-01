package mtg.abilities

import mtg.cards.text.{InstructionParagraph, SimpleInstructionParagraph, SingleAbilityTextParagraph, TextParagraph}
import mtg.continuousEffects.ContinuousEffect
import mtg.core.types.Type
import mtg.core.zones.ZoneType
import mtg.effects.EffectContext
import mtg.game.state.ObjectWithState
import mtg.instructions.TextComponent
import mtg.parts.costs.Cost
import mtg.utils.CaseObjectWithName

sealed trait AbilityDefinition extends TextComponent {
  def isFunctional(objectWithAbility: ObjectWithState): Boolean = {
    getFunctionalZones(objectWithAbility).contains(objectWithAbility.gameObject.zone.zoneType)
  }
  def getFunctionalZones(objectWithAbility: ObjectWithState): Set[ZoneType] = {
    if (objectWithAbility.characteristics.types.intersect(Seq(Type.Instant, Type.Sorcery)).nonEmpty)
      Set(ZoneType.Stack)
    else
      Set(ZoneType.Battlefield)
  }
  def getQuotedDescription(cardName: String): String = "\"" + getText(cardName) + "\""
}

trait StaticAbility extends AbilityDefinition {
  def getEffects(effectContext: EffectContext): Seq[ContinuousEffect]
}

sealed trait AbilityParagraph extends AbilityDefinition with SingleAbilityTextParagraph {
  override def abilityDefinition: AbilityParagraph = this
}

trait StaticAbilityParagraph extends StaticAbility with AbilityParagraph

sealed trait ActivatedOrTriggeredAbilityDefinition extends AbilityParagraph {
  def instructions: InstructionParagraph
}

case class ActivatedAbilityDefinition(
    costs: Seq[Cost],
    instructions: InstructionParagraph)
  extends ActivatedOrTriggeredAbilityDefinition
{
  override def getText(cardName: String): String = costs.map(_.text).mkString(", ") + ": " + instructions.getText(cardName)

  override def getFunctionalZones(objectWithAbility: ObjectWithState): Set[ZoneType] = {
    instructions.functionalZones getOrElse super.getFunctionalZones(objectWithAbility)
  }

  def isManaAbility: Boolean = {
    instructions.asOptionalInstanceOf[SimpleInstructionParagraph]
      .exists(p => p.instructions.forall(_.targetIdentifiers.isEmpty) && p.instructions.exists(_.couldAddMana))
  }
}

case class TriggeredAbilityDefinition(
    triggerCondition: TriggerCondition,
    instructions: InstructionParagraph)
  extends ActivatedOrTriggeredAbilityDefinition
{
  override def getText(cardName: String): String = triggerCondition.getText(cardName).capitalize + ", " + instructions.getUncapitalizedText(cardName)
}

trait KeywordAbility extends StaticAbility with CaseObjectWithName {
  override def getText(cardName: String): String = name.toLowerCase
  override def getQuotedDescription(cardName: String): String = name.toLowerCase
}

case class SpellAbility(instructions: InstructionParagraph) extends AbilityParagraph {
  override def getText(cardName: String): String = instructions.getText(cardName)
}
