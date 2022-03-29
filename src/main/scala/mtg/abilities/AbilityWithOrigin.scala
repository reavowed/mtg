package mtg.abilities

trait AbilityWithOrigin {
  def abilityDefinition: AbilityDefinition
  def origin: AbilityOrigin
}

object AbilityWithOrigin {
  case class KeywordAbilityWithOrigin(abilityDefinition: KeywordAbility, origin: AbilityOrigin) extends AbilityWithOrigin
  case class AbilityParagraphWithOrigin(abilityDefinition: AbilityParagraph, origin: AbilityOrigin) extends AbilityWithOrigin
}
