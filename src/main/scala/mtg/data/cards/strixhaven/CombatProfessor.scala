package mtg.data.cards.strixhaven

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.keyword.{Flying, Vigilance}
import mtg.cards.patterns.Creature
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.CreatureType.{Bird, Cleric}
import mtg.core.types.Type.Creature
import mtg.parts.costs.ManaCost

object CombatProfessor extends Creature(
  "Combat Professor",
  ManaCost(3, White),
  Seq(Bird, Cleric),
  Seq(
    Flying,
    at(beginningOfCombat(you), target(Creature(you.control))(gets(1, 0), gains(Vigilance)).until(endOfTurn))),
  (2, 3)
)
