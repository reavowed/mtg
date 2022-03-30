package mtg.data.sets.strixhaven.cards

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.builder.TypeConversions._
import mtg.abilities.keyword.Hexproof
import mtg.cards.patterns.SpellCard
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.Type.{Creature, Instant}
import mtg.instructions.joiners.And
import mtg.instructions.nounPhrases.{Target, You}
import mtg.instructions.verbs.{Control, Gain, Get}
import mtg.parts.costs.ManaCost

object BeamingDefiance extends SpellCard(
  "Beaming Defiance",
  ManaCost(1, White),
  Instant,
  Target(Creature(You(Control)))(And(Get(+2, +2), Gain(Hexproof)), endOfTurn))
