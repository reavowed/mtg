package mtg.data.cards.strixhaven

import mtg.abilities.builder.EffectBuilder._
import mtg.cards.patterns.Spell
import mtg.core.types.SpellType.Lesson
import mtg.core.types.Type
import mtg.parts.costs.ManaCost

object IntroductionToProphecy extends Spell(
  "Introduction to Prophecy",
  ManaCost(3),
  Type.Sorcery,
  Seq(Lesson),
  scry(2).`then`(drawACard))
