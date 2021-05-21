package mtg.web.visibleState

import mtg.game.objects.{CardObject, GameObject, ObjectId}
import mtg.game.state.PermanentStatus

sealed trait VisibleGameObject
object VisibleGameObject {
  def apply(gameObject: GameObject): VisibleGameObject = gameObject match {
    case cardObject: CardObject => VisibleCard.fromCard(cardObject)
  }
}

case class VisibleCard(name: String, set: String, collectorNumber: Int, objectId: ObjectId, permanentStatus: Option[PermanentStatus]) extends VisibleGameObject
object VisibleCard {
  def fromCard(cardObject: CardObject): VisibleCard = VisibleCard(
    cardObject.card.printing.cardDefinition.name,
    cardObject.card.printing.set.code,
    cardObject.card.printing.collectorNumber,
    cardObject.objectId,
    cardObject.permanentStatus)
}
