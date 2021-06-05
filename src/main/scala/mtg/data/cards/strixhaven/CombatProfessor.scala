package mtg.data.cards.strixhaven

import mtg.abilities.builder.EffectBuilder._
import mtg.abilities.keyword.{Flying, Vigilance}
import mtg.cards.patterns.Creature
import mtg.characteristics.Color.White
import mtg.characteristics.types.CreatureType.{Bird, Cleric}
import mtg.characteristics.types.Type.Creature
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
