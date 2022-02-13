package mtg.data.cards.strixhaven

import mtg.abilities.builder.EffectBuilder._
import mtg.cards.patterns.Spell
import mtg.characteristics.types.Type.{Creature, Instant}
import mtg.core.symbols.ManaSymbol.White
import mtg.parts.costs.ManaCost

object DefendTheCampus extends Spell(
  "Defend the Campus",
  ManaCost(3, White),
  Instant,
  Nil,
  chooseOne(
    Creature(you.control)(get(1, 1)).until(endOfTurn),
    destroy(target(Creature(withPower(4.orGreater))))))
