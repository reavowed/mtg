package mtg.abilities

import mtg.cards.text.{InstructionParagraph, SimpleInstructionParagraph, TextParagraph}
import mtg.continuousEffects.ContinuousEffect
import mtg.core.types.Type
import mtg.core.zones.ZoneType
import mtg.effects.condition.Condition
import mtg.game.state.ObjectWithState
import mtg.parts.costs.Cost
import mtg.utils.CaseObjectWithName
import mtg.utils.TextUtils._

sealed abstract class AbilityDefinition {
  def functionalZones: Set[ZoneType] = Set(ZoneType.Battlefield)
  def isFunctional(objectWithAbility: ObjectWithState): Boolean = {
    getFunctionalZones(objectWithAbility).contains(objectWithAbility.gameObject.zone.zoneType)
  }
  def getFunctionalZones(objectWithAbility: ObjectWithState): Set[ZoneType] = {
    if (objectWithAbility.characteristics.types.intersect(Seq(Type.Instant, Type.Sorcery)).nonEmpty)
      Set(ZoneType.Stack)
    else
      Set(ZoneType.Battlefield)
  }
  def getText(cardName: String): String
  def getQuotedDescription(cardName: String): String = "\"" + getText(cardName) + "\""
}

sealed trait ActivatedOrTriggeredAbilityDefinition extends AbilityDefinition {
  def instructions: InstructionParagraph
}

case class ActivatedAbilityDefinition(
    costs: Seq[Cost],
    instructions: InstructionParagraph)
  extends ActivatedOrTriggeredAbilityDefinition with TextParagraph
{
  override def getText(cardName: String): String = costs.map(_.text).mkString(", ") + ": " + instructions.getText(cardName)
  override def abilityDefinitions: Seq[AbilityDefinition] = Seq(this)

  def isManaAbility: Boolean = {
    instructions.asOptionalInstanceOf[SimpleInstructionParagraph]
      .exists(p => p.instructions.forall(_.targetIdentifiers.isEmpty) && p.instructions.exists(_.couldAddMana))
  }
}

case class TriggeredAbilityDefinition(
    condition: Condition,
    instructions: InstructionParagraph)
  extends ActivatedOrTriggeredAbilityDefinition with TextParagraph
{
  override def getText(cardName: String): String = "At " + condition.getText(cardName) + ", " + instructions.getText(cardName).uncapitalize
  override def abilityDefinitions: Seq[AbilityDefinition] = Seq(this)
}

trait KeywordAbility extends AbilityDefinition with CaseObjectWithName {
  override def getText(cardName: String): String = name.toLowerCase
  override def getQuotedDescription(cardName: String): String = name.toLowerCase
}

case class SpellAbility(instructions: InstructionParagraph) extends AbilityDefinition {
  override def getText(cardName: String): String = instructions.getText(cardName)
}

abstract class StaticAbility extends AbilityDefinition {
  def getEffects(objectWithAbility: ObjectWithState): Seq[ContinuousEffect]
}
