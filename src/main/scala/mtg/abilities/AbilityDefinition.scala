package mtg.abilities

import mtg.effects.Effect
import mtg.game.ZoneType
import mtg.parts.costs.Cost

sealed abstract class AbilityDefinition {
  def functionalZones: Set[ZoneType] = Set(ZoneType.Battlefield)
  def text: String
}

case class ActivatedAbilityDefinition(
    costs: Seq[Cost],
    effects: Seq[Effect])
  extends AbilityDefinition
{
  override def text: String = costs.map(_.text).mkString(", ") + ": " + effects.map(_.text).mkString(" ")
}
