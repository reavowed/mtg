package mtg.cards.patterns

import mtg.cards.CardDefinition
import mtg.characteristics.types.{BasicLandType, Supertype, Types}

class BasicLand(val basicLandType: BasicLandType) extends CardDefinition(
  basicLandType.name,
  None,
  None,
  Seq(Supertype.Basic),
  Seq(Types.Land),
  Seq(basicLandType),
  s"({T}: Add ${basicLandType.color.manaType.symbol.text}.)",
  Nil,
  None,
  None)
