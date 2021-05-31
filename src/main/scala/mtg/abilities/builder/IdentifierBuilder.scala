package mtg.abilities.builder

import mtg.effects.identifiers.{Identifier, ItIdentifier, ThisIdentifier, YouIdentifier}
import mtg.game.{ObjectId, PlayerId}

trait IdentifierBuilder {
  def it: Identifier[ObjectId] = ItIdentifier
  def you: Identifier[PlayerId] = YouIdentifier
  def `this`: Identifier[ObjectId] = ThisIdentifier
}
