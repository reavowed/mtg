package mtg.web.visibleState

import mtg.abilities.TriggeredAbility
import mtg.game.objects.{AbilityOnTheStack, Card, UnderlyingObject}
import mtg.game.state.GameState

import scala.annotation.tailrec

case class ArtDetails(set: String, collectorNumber: Int)

object ArtDetails {
  @tailrec
  def get(underlyingObject: UnderlyingObject, gameState: GameState): ArtDetails = underlyingObject match {
    case card: Card =>
      ArtDetails(card.printing.set.code, card.printing.collectorNumber)
    case abilityOnTheStack: AbilityOnTheStack =>
      get(gameState.gameObjectState.getCurrentOrLastKnownState(abilityOnTheStack.source).gameObject.underlyingObject, gameState)
  }
  def get(triggeredAbility: TriggeredAbility, gameState: GameState): ArtDetails = {
    get(gameState.gameObjectState.getCurrentOrLastKnownState(triggeredAbility.sourceId).gameObject.underlyingObject, gameState)
  }
}
