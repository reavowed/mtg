package mtg

import mtg.cards.CardDefinition
import mtg.cards.patterns.VanillaCreatureCard
import mtg.parts.costs.ManaCost

object TestCards {
  def vanillaCreature(power: Int, toughness: Int): CardDefinition = new VanillaCreatureCard(
    s"Test Vanilla $power/$toughness",
    ManaCost(0),
    Nil,
    (power, toughness))
}
