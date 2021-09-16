package mtg.data.cards.strixhaven

import mtg.abilities.builder.EffectBuilder._
import mtg.cards.patterns.Spell
import mtg.cards.text.SimpleSpellEffectParagraph
import mtg.characteristics.types.SpellType.Lesson
import mtg.characteristics.types.Type
import mtg.characteristics.types.Type.Land
import mtg.parts.costs.ManaCost

object IntroductionToAnnihilation extends Spell(
  "Introduction to Annihilation",
  ManaCost(5),
  Type.Sorcery,
  Seq(Lesson),
  SimpleSpellEffectParagraph(
    exile(target(permanent(non(Land)))),
    it.s(controller).drawsACard))
