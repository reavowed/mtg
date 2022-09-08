package mtg.sets.strixhaven.cards

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.CreatureCard
import mtg.definitions.symbols.ManaSymbol.White
import mtg.definitions.types.BasicLandType.Plains
import mtg.definitions.types.CreatureType.Spirit
import mtg.definitions.types.Supertype.Basic
import mtg.instructions.conditions.When
import mtg.instructions.joiners.{May, Then}
import mtg.instructions.nounPhrases.{CardName, It, You}
import mtg.instructions.nouns.Card
import mtg.instructions.verbs._
import mtg.parts.costs.ManaCost

object PilgrimOfTheAges extends CreatureCard(
  "Pilgrim of the Ages",
  ManaCost(2, White),
  Seq(Spirit),
  Seq(
    When(CardName, EntersTheBattlefield)(You(May(Then(SearchYourLibraryFor(Basic(Plains(Card))), Reveal(It), PutIntoYourHand(It), Shuffle)))),
    activatedAbility(ManaCost(6))(ReturnFromYourGraveyardToYourHand(CardName))),
  (2, 1))
