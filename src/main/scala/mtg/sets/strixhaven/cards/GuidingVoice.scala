package mtg.sets.strixhaven.cards

import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.SpellCard
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.Type
import mtg.core.types.Type.Creature
import mtg.instructions.nounPhrases.Target
import mtg.instructions.verbs.Put
import mtg.parts.Counter
import mtg.parts.costs.ManaCost
import mtg.sets.strixhaven.abilities.Learn

object GuidingVoice extends SpellCard(
  "Guiding Voice",
  ManaCost(White),
  Type.Sorcery,
  Nil,
  Seq(
    Put(1, Counter.PlusOnePlusOne)(Target(Creature)),
    Learn))
