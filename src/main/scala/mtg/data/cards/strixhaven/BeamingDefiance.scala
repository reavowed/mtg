package mtg.data.cards.strixhaven

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.keyword.Hexproof
import mtg.cards.patterns.SpellCard
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.Type.{Creature, Instant}
import mtg.instructions.nounPhrases.{Target, You}
import mtg.instructions.verbs.Control
import mtg.parts.costs.ManaCost
import mtg.abilities.builder.TypeConversions._

object BeamingDefiance extends SpellCard(
  "Beaming Defiance",
  ManaCost(1, White),
  Instant,
  Target(Creature(You(Control)))(gets(+2, +2), gains(Hexproof)).until(endOfTurn)
)
