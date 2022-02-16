package mtg.data.cards.strixhaven

import mtg.abilities.builder.EffectBuilder._
import mtg.cards.patterns.Spell
import mtg.cards.text.SimpleSpellEffectParagraph
import mtg.core.types.SpellType.Lesson
import mtg.core.types.Supertype.Basic
import mtg.core.types.Type
import mtg.core.types.Type.Land
import mtg.parts.costs.ManaCost

object EnvironmentalSciences extends Spell(
  "Environmental Sciences",
  ManaCost(2),
  Type.Sorcery,
  Seq(Lesson),
  SimpleSpellEffectParagraph(
    (searchYourLibraryForA(card(Basic, Land)), reveal(it), put(it).intoYourHand).`then`(shuffle),
    you.gain(2).life))
