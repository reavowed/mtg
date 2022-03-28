package mtg.data.cards.alpha

import mtg.abilities.builder.InstructionBuilder._
import mtg.cards.patterns.SpellCard
import mtg.core.symbols.ManaSymbol.Red
import mtg.core.types.Type
import mtg.instructions.actions.DealDamage
import mtg.instructions.nouns.CardName
import mtg.parts.costs.ManaCost

object LightningBolt extends SpellCard(
  "Lightning Bolt",
  ManaCost(Red),
  Type.Instant,
  CardName(DealDamage(3)(anyTarget)))
