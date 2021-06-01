package mtg.abilities.builder

import mtg.effects.identifiers.{CardNameIdentifier, ControllerIdentifier, Identifier, ItIdentifier, YouIdentifier}
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

trait IdentifierBuilder {
  def it: Identifier[ObjectId] = ItIdentifier
  def you: Identifier[PlayerId] = YouIdentifier
  def cardName: Identifier[ObjectId] = CardNameIdentifier

  implicit class ObjectIdentifierExtensions(objectIdentifier: Identifier[ObjectId]) {
    def s[T <: ObjectOrPlayer](f: Identifier[ObjectId] => Identifier[T]): Identifier[T] = f(objectIdentifier)
  }
  def controller: Identifier[ObjectId] => Identifier[PlayerId] = ControllerIdentifier
}
