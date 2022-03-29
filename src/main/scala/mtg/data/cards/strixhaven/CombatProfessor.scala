package mtg.data.cards.strixhaven

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.keyword.{Flying, Vigilance}
import mtg.cards.patterns.CreatureCard
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.CreatureType.{Bird, Cleric}
import mtg.core.types.Type.Creature
import mtg.instructions.conditions.At
import mtg.instructions.nounPhrases.{Target, You}
import mtg.parts.costs.ManaCost
import mtg.abilities.builder.TypeConversions._
import mtg.instructions.verbs.Control

object CombatProfessor extends CreatureCard(
  "Combat Professor",
  ManaCost(3, White),
  Seq(Bird, Cleric),
  Seq(
    Flying,
    At(beginningOfCombat(You))(Target(Creature(You(Control)))(gets(1, 0), gains(Vigilance)).until(endOfTurn))),
  (2, 3)
)
