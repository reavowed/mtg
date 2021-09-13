package mtg.data.cards.alpha

import mtg.abilities.builder.EffectBuilder._
import mtg.cards.patterns.Spell
import mtg.characteristics.Color.Red
import mtg.characteristics.types.Type
import mtg.parts.costs.ManaCost

object LightningBolt extends Spell(
  "Lightning Bolt",
  ManaCost(Red),
  Type.Instant,
  Nil,
  cardName.deals(3).damageTo(anyTarget))
