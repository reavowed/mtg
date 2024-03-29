package mtg.sets.alpha.cards

import mtg.cards.patterns.SpellCard
import mtg.definitions.symbols.ManaSymbol.Red
import mtg.definitions.types.Type
import mtg.instructions.nounPhrases.{AnyTarget, CardName}
import mtg.instructions.verbs.DealDamage
import mtg.parts.costs.ManaCost

object LightningBolt extends SpellCard(
  "Lightning Bolt",
  ManaCost(Red),
  Type.Instant,
  CardName(DealDamage(3)(AnyTarget)))
