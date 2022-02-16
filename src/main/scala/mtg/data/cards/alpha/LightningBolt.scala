package mtg.data.cards.alpha

import mtg.abilities.builder.EffectBuilder._
import mtg.cards.patterns.Spell
import mtg.core.symbols.ManaSymbol.Red
import mtg.core.types.Type
import mtg.parts.costs.ManaCost

object LightningBolt extends Spell(
  "Lightning Bolt",
  ManaCost(Red),
  Type.Instant,
  Nil,
  cardName.deals(3).damageTo(anyTarget))
