package mtg.game.state

import mtg.game.objects.{GameObjectState, ObjectId}

case class DerivedState(objectStates: Map[ObjectId, ObjectWithState]) {
  def allObjectStates: Seq[ObjectWithState] = objectStates.values.toSeq
}

object DerivedState {
  def calculateFromGameObjectState(gameObjectState: GameObjectState): DerivedState = {
    val initialStates = gameObjectState.allObjects.map(gameObject =>
      gameObject.objectId -> ObjectWithState(gameObject, gameObject.baseCharacteristics, None)
    ).toMap

    DerivedState(initialStates)
  }
}
