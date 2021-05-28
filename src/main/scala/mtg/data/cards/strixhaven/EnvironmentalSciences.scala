package mtg.data.cards.strixhaven

import mtg.cards.patterns.Spell
import mtg.characteristics.types.SpellType.Lesson
import mtg.characteristics.types.Type
import mtg.parts.costs.ManaCost
import mtg.abilities.AbilityBuilder._
import mtg.abilities.AbilityParagraph

object EnvironmentalSciences extends Spell(
  "Environmental Sciences",
  ManaCost(2),
  Type.Sorcery,
  Seq(Lesson),
  Seq(
    AbilityParagraph(
      Seq(searchYourLibraryForA(basicLand), reveal(it), putIntoYourHand(it)).then(shuffle),
      youGainLife(2))))
