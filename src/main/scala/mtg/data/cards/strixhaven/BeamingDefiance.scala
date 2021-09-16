package mtg.data.cards.strixhaven

import mtg.abilities.builder.EffectBuilder._
import mtg.abilities.keyword.Hexproof
import mtg.cards.patterns.Spell
import mtg.characteristics.Color.White
import mtg.characteristics.types.Type.{Creature, Instant}
import mtg.parts.costs.ManaCost

object BeamingDefiance extends Spell(
  "Beaming Defiance",
  ManaCost(1, White),
  Instant,
  Nil,
  target(Creature(you.control))(gets(+2, +2), gains(Hexproof)).until(endOfTurn)
)
