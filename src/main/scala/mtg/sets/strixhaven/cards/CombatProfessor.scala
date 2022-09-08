package mtg.sets.strixhaven.cards

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.builder.TypeConversions._
import mtg.abilities.keyword.{Flying, Vigilance}
import mtg.cards.patterns.CreatureCard
import mtg.definitions.symbols.ManaSymbol.White
import mtg.definitions.types.CreatureType.{Bird, Cleric}
import mtg.definitions.types.Type.Creature
import mtg.instructions.conditions.At
import mtg.instructions.joiners.And
import mtg.instructions.nounPhrases.{Target, You}
import mtg.instructions.verbs.{Control, Gain, Get}
import mtg.parts.costs.ManaCost

object CombatProfessor extends CreatureCard(
  "Combat Professor",
  ManaCost(3, White),
  Seq(Bird, Cleric),
  Seq(
    Flying,
    At(beginningOfCombat(You))(Target(Creature(You(Control)))(And(Get(+1, +0), Gain(Vigilance)), endOfTurn))),
  (2, 3))
