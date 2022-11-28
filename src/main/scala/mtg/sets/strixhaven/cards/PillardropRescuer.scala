package mtg.sets.strixhaven.cards

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.builder.TypeConversions._
import mtg.abilities.keyword.Flying
import mtg.cards.patterns.CreatureCard
import mtg.definitions.symbols.ManaSymbol.White
import mtg.definitions.types.CreatureType.{Cleric, Spirit}
import mtg.definitions.types.Type.Creature
import mtg.instructions.conditions.When
import mtg.instructions.nounPhrases.{CardName, Target}
import mtg.instructions.nouns.Card
import mtg.instructions.suffixDescriptors.WithManaValue
import mtg.instructions.verbs._
import mtg.parts.costs.ManaCost

object PillardropRescuer extends CreatureCard(
  "Pillardrop Rescuer",
  ManaCost(4, White),
  Seq(Spirit, Cleric),
  Seq(
    Flying,
    When(CardName, EntersTheBattlefield)(ReturnFromYourGraveyardToYourHand(Target(Creature(Card(WithManaValue(3.orLess))))))),
  (2, 2))
