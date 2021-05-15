package mtg.web

import mtg.game.PlayerIdentifier
import mtg.game.objects.{CardObject, GameObject}
import mtg.game.state.GameStateManager

case class VisibleState(hand: Seq[VisibleGameObject])

sealed trait VisibleGameObject
case class VisibleCard(name: String, set: String, collectorNumber: Int, objectId: Int) extends VisibleGameObject

object VisibleState {
  def forPlayer(playerIdentifier: PlayerIdentifier, gameState: GameState): VisibleState = {
    VisibleState(gameState.gameObjectState.hands(playerIdentifier).map(convertGameObject))
  }

  def convertGameObject(gameObject: GameObject) = {
    gameObject match {
      case cardObject: CardObject =>
        VisibleCard(cardObject.card.printing.cardDefinition.name, cardObject.card.printing.set.code, cardObject.card.printing.collectorNumber, cardObject.objectId.sequentialId)
    }
  }
}
