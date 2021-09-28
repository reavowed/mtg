package mtg.abilities

import mtg.cards.text.{SimpleSpellEffectParagraph, SpellEffectParagraph, TextParagraph}
import mtg.characteristics.types.Type.{Instant, Sorcery}
import mtg.effects.condition.ConditionDefinition
import mtg.effects.oneshot.basic.AddManaEffect
import mtg.effects.{ContinuousEffect, OneShotEffect}
import mtg.game.ZoneType
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
    if (objectWithAbility.characteristics.types.intersect(Seq(Instant, Sorcery)).nonEmpty)
      Set(ZoneType.Stack)
    else
      Set(ZoneType.Battlefield)
  }
  def getText(cardName: String): String
  def getQuotedDescription(cardName: String): String = "\"" + getText(cardName) + "\""
}

sealed trait ActivatedOrTriggeredAbilityDefinition extends AbilityDefinition {
  def effectParagraph: SpellEffectParagraph
}

case class ActivatedAbilityDefinition(
    costs: Seq[Cost],
    effectParagraph: SpellEffectParagraph)
  extends ActivatedOrTriggeredAbilityDefinition
{
  override def getText(cardName: String): String = costs.map(_.text).mkString(", ") + ": " + effectParagraph.getText(cardName)

  def isManaAbility: Boolean = {
    effectParagraph.asOptionalInstanceOf[SimpleSpellEffectParagraph]
      .exists(p => p.effects.forall(_.targetIdentifiers.isEmpty) && p.effects.exists(_.isInstanceOf[AddManaEffect]))
  }
}

case class TriggeredAbilityDefinition(
    condition: ConditionDefinition,
    effectParagraph: SpellEffectParagraph)
  extends ActivatedOrTriggeredAbilityDefinition with TextParagraph {
  override def getText(cardName: String): String = "At " + condition.getText(cardName) + ", " + effectParagraph.getText(cardName).uncapitalize
  override def abilityDefinitions: Seq[AbilityDefinition] = Seq(this)
}

trait KeywordAbility extends AbilityDefinition with CaseObjectWithName {
  override def getText(cardName: String): String = name.toLowerCase
  override def getQuotedDescription(cardName: String): String = name.toLowerCase
}

case class SpellAbility(effectParagraph: SpellEffectParagraph) extends AbilityDefinition {
  override def getText(cardName: String): String = effectParagraph.getText(cardName)
}

abstract class StaticAbility extends AbilityDefinition {
  def getEffects(objectWithAbility: ObjectWithState): Seq[ContinuousEffect]
}
