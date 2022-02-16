package mtg.abilities.builder

import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.effects.identifiers._

trait IdentifierBuilder {
  def it: SingleIdentifier[ObjectId] = ItIdentifier
  def you: StaticIdentifier[PlayerId] = YouIdentifier
  def cardName: SingleIdentifier[ObjectId] = CardNameIdentifier

  implicit class ObjectIdentifierExtensions(objectIdentifier: SingleIdentifier[ObjectId]) {
    def s[T <: ObjectOrPlayerId](f: SingleIdentifier[ObjectId] => SingleIdentifier[T]): SingleIdentifier[T] = f(objectIdentifier)
  }
  def controller: SingleIdentifier[ObjectId] => SingleIdentifier[PlayerId] = ControllerIdentifier
}
