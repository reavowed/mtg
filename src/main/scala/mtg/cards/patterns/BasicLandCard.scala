package mtg.cards.patterns

import mtg.cards.CardDefinition
import mtg.definitions.types.{BasicLandType, Supertype, Type}

class BasicLandCard(val basicLandType: BasicLandType) extends CardDefinition(
  basicLandType.name,
  None,
  None,
  Seq(Supertype.Basic),
  Seq(Type.Land),
  Seq(basicLandType),
  Nil,
  None,
  None)
