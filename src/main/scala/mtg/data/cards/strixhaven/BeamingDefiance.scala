package mtg.data.cards.strixhaven

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.keyword.Hexproof
import mtg.cards.patterns.SpellCard
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.Type.{Creature, Instant}
import mtg.parts.costs.ManaCost

object BeamingDefiance extends SpellCard(
  "Beaming Defiance",
  ManaCost(1, White),
  Instant,
  Nil,
  target(Creature(you.control))(gets(+2, +2), gains(Hexproof)).until(endOfTurn)
)
