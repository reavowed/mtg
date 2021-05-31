package mtg.abilities

import mtg.cards.text.SpellEffectParagraph
import mtg.characteristics.types.Type.{Instant, Sorcery}
import mtg.effects.{ContinuousEffect, OneShotEffect}
import mtg.game.ZoneType
import mtg.game.state.ObjectWithState
import mtg.parts.costs.Cost
import mtg.utils.CaseObjectWithName

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

case class ActivatedAbilityDefinition(
    costs: Seq[Cost],
    effectParagraph: SpellEffectParagraph)
  extends AbilityDefinition
{
  override def getText(cardName: String): String = costs.map(_.text).mkString(", ") + ": " + effectParagraph.getText(cardName)
}

trait KeywordAbility extends AbilityDefinition with CaseObjectWithName {
  override def getText(cardName: String): String = name.toLowerCase
  override def getQuotedDescription(cardName: String): String = name.toLowerCase
}

case class SpellAbility(effectParagraph: SpellEffectParagraph) extends AbilityDefinition {
  override def getText(cardName: String): String = effectParagraph.getText(cardName)
  def effects: Seq[OneShotEffect] = effectParagraph.effects
}

abstract class StaticAbility extends AbilityDefinition {
  def getEffects(objectWithAbility: ObjectWithState): Seq[ContinuousEffect]
}
