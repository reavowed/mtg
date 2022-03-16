package mtg.data.cards.strixhaven

import mtg.abilities.builder.InstructionBuilder._
import mtg.cards.patterns.Spell
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.Type.{Creature, Instant}
import mtg.instructions.actions.Destroy
import mtg.parts.costs.ManaCost

object DefendTheCampus extends Spell(
  "Defend the Campus",
  ManaCost(3, White),
  Instant,
  Nil,
  chooseOne(
    Creature(you.control)(get(1, 1)).until(endOfTurn),
    Destroy(target(Creature(withPower(4.orGreater))))))
