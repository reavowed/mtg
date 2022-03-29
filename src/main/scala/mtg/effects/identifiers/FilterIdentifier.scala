package mtg.effects.identifiers

import mtg.core.ObjectOrPlayerId
import mtg.effects.StackObjectResolutionContext
import mtg.effects.filters.Filter
import mtg.game.state.GameState
import mtg.instructions.nouns.SetIdentifyingNounPhrase
import mtg.text.{VerbNumber, VerbPerson}

import scala.reflect.ClassTag

case class FilterIdentifier[T <: ObjectOrPlayerId : ClassTag](filter: Filter[T]) extends SetIdentifyingNounPhrase[T] {
  override def getText(cardName: String): String = filter.getPlural(cardName)
  override def person: VerbPerson = VerbPerson.Third
  override def number: VerbNumber = VerbNumber.Plural
  override def identifyAll(gameState: GameState, resolutionContext: StackObjectResolutionContext): (Seq[T], StackObjectResolutionContext) = {
    val matches = (gameState.gameData.playersInTurnOrder ++ gameState.gameObjectState.allObjects.map(_.objectId))
      .ofType[T]
      .filter(filter.describes(_, gameState, resolutionContext))
    (matches, resolutionContext)
  }
}
