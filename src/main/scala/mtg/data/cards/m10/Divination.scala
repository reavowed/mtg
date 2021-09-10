package mtg.data.cards.m10

import mtg.abilities.builder.EffectBuilder._
import mtg.cards.patterns.Spell
import mtg.characteristics.Color.Blue
import mtg.characteristics.types.Type
import mtg.parts.costs.ManaCost

object Divination extends Spell(
  "Divination",
  ManaCost(2, Blue),
  Type.Sorcery,
  Nil,
  draw(2).cards)
