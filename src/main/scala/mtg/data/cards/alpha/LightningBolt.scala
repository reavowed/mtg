package mtg.data.cards.alpha

import mtg.cards.patterns.SpellCard
import mtg.core.symbols.ManaSymbol.Red
import mtg.core.types.Type
import mtg.effects.targets.AnyTarget
import mtg.instructions.nouns.CardName
import mtg.instructions.verbs.DealDamage
import mtg.parts.costs.ManaCost

object LightningBolt extends SpellCard(
  "Lightning Bolt",
  ManaCost(Red),
  Type.Instant,
  CardName(DealDamage(3)(AnyTarget)))
