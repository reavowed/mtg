package mtg.data.sets.strixhaven.cards

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.SpellCard
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.Type.{Creature, Instant}
import mtg.instructions.nounPhrases.{Target, You}
import mtg.instructions.suffixDescriptors.WithPower
import mtg.instructions.verbs.{Control, Destroy, Get}
import mtg.parts.costs.ManaCost

object DefendTheCampus extends SpellCard(
  "Defend the Campus",
  ManaCost(3, White),
  Instant,
  chooseOne(
    Creature(You(Control))(Get(+1, +1), endOfTurn),
    Destroy(Target(Creature(WithPower(4.orGreater))))))
