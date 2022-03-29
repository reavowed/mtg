package mtg.data.cards.strixhaven

import mtg.abilities.builder.InstructionBuilder._
import mtg.cards.patterns.SpellCard
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.Type.{Creature, Instant}
import mtg.instructions.nounPhrases.{Target, You}
import mtg.instructions.verbs.{Control, Destroy}
import mtg.parts.costs.ManaCost
import mtg.abilities.builder.TypeConversions._

object DefendTheCampus extends SpellCard(
  "Defend the Campus",
  ManaCost(3, White),
  Instant,
  chooseOne(
    Creature(You(Control))(get(1, 1)).until(endOfTurn),
    Destroy(Target(Creature(withPower(4.orGreater))))))
