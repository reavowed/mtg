package mtg.cards.patterns

import mtg.cards.CardDefinition
import mtg.core.types.{BasicLandType, Supertype, Type}

class BasicLand(val basicLandType: BasicLandType) extends CardDefinition(
  basicLandType.name,
  None,
  None,
  Seq(Supertype.Basic),
  Seq(Type.Land),
  Seq(basicLandType),
  Nil,
  None,
  None)
