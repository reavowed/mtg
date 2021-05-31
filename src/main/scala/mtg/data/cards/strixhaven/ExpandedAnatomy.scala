package mtg.data.cards.strixhaven

import mtg.cards.patterns.Spell
import mtg.characteristics.types.SpellType.Lesson
import mtg.characteristics.types.Type
import mtg.parts.costs.ManaCost
import mtg.abilities.builder.EffectBuilder._
import mtg.characteristics.types.Type.Creature
import mtg.parts.counters.PlusOnePlusOneCounter

object ExpandedAnatomy extends Spell(
  "Expanded Anatomy",
  ManaCost(3),
  Type.Sorcery,
  Seq(Lesson),
  put(2, PlusOnePlusOneCounter).on(target(Creature))
)
