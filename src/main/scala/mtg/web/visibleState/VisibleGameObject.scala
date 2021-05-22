package mtg.web.visibleState

import mtg.game.objects.{CardObject, GameObject, ObjectId}
import mtg.game.state.{Characteristics, DerivedState, PermanentStatus}

sealed trait VisibleGameObject
object VisibleGameObject {
  def apply(gameObject: GameObject, derivedState: DerivedState): VisibleGameObject = gameObject match {
    case cardObject: CardObject => VisibleCard(cardObject, derivedState)
  }
}

case class VisibleCard(
  name: String,
  set: String,
  collectorNumber: Int,
  objectId: ObjectId,
  characteristics: Characteristics,
  permanentStatus: Option[PermanentStatus]
) extends VisibleGameObject

object VisibleCard {
  def apply(cardObject: CardObject, derivedState: DerivedState): VisibleCard = VisibleCard(
    cardObject.card.printing.cardDefinition.name,
    cardObject.card.printing.set.code,
    cardObject.card.printing.collectorNumber,
    cardObject.objectId,
    derivedState.objectStates(cardObject.objectId).characteristics,
    cardObject.permanentStatus)
}
