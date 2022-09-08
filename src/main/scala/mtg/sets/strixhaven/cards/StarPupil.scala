package mtg.sets.strixhaven.cards

import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.CreatureCard
import mtg.definitions.symbols.ManaSymbol.White
import mtg.definitions.types.CreatureType.{Human, Wizard}
import mtg.definitions.types.Type.Creature
import mtg.instructions.conditions.When
import mtg.instructions.nounPhrases._
import mtg.instructions.suffixDescriptors.With
import mtg.instructions.verbs.{Control, Die, EntersTheBattlefield, Put}
import mtg.parts.Counter
import mtg.parts.costs.ManaCost

object StarPupil extends CreatureCard(
  "Star Pupil",
  ManaCost(White),
  Seq(Human, Wizard),
  Seq(
    CardName(EntersTheBattlefield(With(1, Counter.PlusOnePlusOne))),
    When(CardName, Die)(Put(Counters(It), Target(Creature(You(Control)))))),
  (0, 0))
