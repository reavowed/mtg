package mtg.sets.strixhaven.cards

import mtg.cards.patterns.CreatureCard
import mtg.core.symbols.ManaSymbol.White
import mtg.core.types.BasicLandType.Plains
import mtg.core.types.CreatureType.Spirit
import mtg.core.types.Supertype.Basic
import mtg.instructions.conditions.When
import mtg.instructions.joiners.{May, Then}
import mtg.instructions.nounPhrases.{CardName, It, You}
import mtg.instructions.nouns.Card
import mtg.instructions.verbs.{EntersTheBattlefield, PutIntoYourHand, ReturnFromYourGraveyardToYourHand, Reveal, SearchYourLibraryFor, Shuffle}
import mtg.parts.costs.ManaCost
import mtg.abilities.builder.TypeConversions._
import mtg.abilities.builder.InstructionBuilder._

object PilgrimOfTheAges extends CreatureCard(
  "Pilgrim of the Ages",
  ManaCost(2, White),
  Seq(Spirit),
  Seq(
    When(CardName, EntersTheBattlefield)(You(May(Then(SearchYourLibraryFor(Basic(Plains(Card))), Reveal(It), PutIntoYourHand(It), Shuffle)))),
    activatedAbility(ManaCost(6))(ReturnFromYourGraveyardToYourHand(CardName))),
  (2, 1))
