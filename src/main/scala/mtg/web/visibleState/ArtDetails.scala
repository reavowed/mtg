package mtg.web.visibleState

import mtg.abilities.TriggeredAbility
import mtg.cards.CardPrinting
import mtg.game.objects.{AbilityOnTheStack, Card, UnderlyingObject}
import mtg.game.state.GameState

import scala.annotation.tailrec

case class ArtDetails(set: String, collectorNumber: Int)

object ArtDetails {
  def apply(cardPrinting: CardPrinting): ArtDetails = ArtDetails(cardPrinting.set.code, cardPrinting.collectorNumber)

  @tailrec
  def get(underlyingObject: UnderlyingObject, gameState: GameState): ArtDetails = underlyingObject match {
    case card: Card =>
      ArtDetails(card.printing)
    case abilityOnTheStack: AbilityOnTheStack =>
      get(gameState.gameObjectState.getCurrentOrLastKnownState(abilityOnTheStack.source).gameObject.underlyingObject, gameState)
  }
  def get(triggeredAbility: TriggeredAbility, gameState: GameState): ArtDetails = {
    get(gameState.gameObjectState.getCurrentOrLastKnownState(triggeredAbility.sourceId).gameObject.underlyingObject, gameState)
  }
}
