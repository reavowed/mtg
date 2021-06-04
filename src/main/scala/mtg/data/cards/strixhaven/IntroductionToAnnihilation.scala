package mtg.data.cards.strixhaven

import mtg.abilities.builder.EffectBuilder._
import mtg.cards.patterns.Spell
import mtg.cards.text.SpellEffectParagraph
import mtg.characteristics.types.SpellType.Lesson
import mtg.characteristics.types.Type
import mtg.characteristics.types.Type.{Creature, Land}
import mtg.parts.costs.ManaCost

object IntroductionToAnnihilation extends Spell(
  "Introduction to Annihilation",
  ManaCost(5),
  Type.Sorcery,
  Seq(Lesson),
  SpellEffectParagraph(
    exile(target(permanent(non(Land)))),
    it.s(controller).drawsACard))
