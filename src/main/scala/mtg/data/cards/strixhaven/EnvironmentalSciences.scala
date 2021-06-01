package mtg.data.cards.strixhaven

import mtg.abilities.builder.EffectBuilder._
import mtg.cards.patterns.Spell
import mtg.cards.text.SpellEffectParagraph
import mtg.characteristics.types.SpellType.Lesson
import mtg.characteristics.types.Supertype.Basic
import mtg.characteristics.types.Type
import mtg.characteristics.types.Type.Land
import mtg.parts.costs.ManaCost

object EnvironmentalSciences extends Spell(
  "Environmental Sciences",
  ManaCost(2),
  Type.Sorcery,
  Seq(Lesson),
  SpellEffectParagraph(
    (searchYourLibraryForA(Basic(Land).card), reveal(it), put(it).intoYourHand).`then`(shuffle),
    you.gain(2).life))