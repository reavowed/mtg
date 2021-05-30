package mtg.data.cards.strixhaven

import mtg.cards.patterns.Spell
import mtg.characteristics.types.SpellType.Lesson
import mtg.characteristics.types.Type
import mtg.parts.costs.ManaCost
import mtg.abilities.EffectBuilder._
import mtg.abilities.EffectParagraph

object EnvironmentalSciences extends Spell(
  "Environmental Sciences",
  ManaCost(2),
  Type.Sorcery,
  Seq(Lesson),
  EffectParagraph(
    (searchYourLibraryForA(basicLand), reveal(it), put(it).intoYourHand).`then`(shuffle),
    you.gain(2).life))
