package mtg.abilities.builder

import mtg.effects.identifiers._
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}

trait IdentifierBuilder {
  def it: SingleIdentifier[ObjectId] = ItIdentifier
  def you: StaticIdentifier[PlayerId] = YouIdentifier
  def cardName: SingleIdentifier[ObjectId] = CardNameIdentifier

  implicit class ObjectIdentifierExtensions(objectIdentifier: SingleIdentifier[ObjectId]) {
    def s[T <: ObjectOrPlayer](f: SingleIdentifier[ObjectId] => SingleIdentifier[T]): SingleIdentifier[T] = f(objectIdentifier)
  }
  def controller: SingleIdentifier[ObjectId] => SingleIdentifier[PlayerId] = ControllerIdentifier
}
