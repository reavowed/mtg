package mtg.sets.strixhaven.cards

import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.SpellCard
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.Type
import mtg.core.types.Type.Creature
import mtg.instructions.adjectives.Tapped
import mtg.instructions.nounPhrases.Target
import mtg.instructions.verbs.Exile
import mtg.parts.costs.ManaCost

object Expel extends SpellCard(
  "Expel",
  ManaCost(2, White),
  Type.Instant,
  Exile(Target(Tapped(Creature)))
)
