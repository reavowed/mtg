package mtg.game.state

import mtg.characteristics.types.BasicLandType
import mtg.game.objects.{GameObjectState, ObjectId}

case class DerivedState(objectStates: Map[ObjectId, ObjectWithState]) {
  def allObjectStates: Seq[ObjectWithState] = objectStates.values.toSeq
}

object DerivedState {
  def calculateFromGameObjectState(gameObjectState: GameObjectState): DerivedState = {
    val initialStates = gameObjectState.allObjects.map(gameObject => ObjectWithState(gameObject, gameObject.baseCharacteristics, gameObject.defaultController))

    val finalStates = Seq(
      addIntrinsicManaAbilities(_)
    ).foldLeft(initialStates) { (objectStates, updater) => updater(objectStates) }

    DerivedState(finalStates.map(objectWithState => objectWithState.gameObject.objectId -> objectWithState).toMap)
  }

  def addIntrinsicManaAbilities(objectStates: Seq[ObjectWithState]): Seq[ObjectWithState] = {
    objectStates.map { objectWithState =>
      objectWithState.characteristics.subTypes.ofType[BasicLandType].foldLeft(objectWithState) { (objectWithState, landType) =>
        objectWithState.addAbility(landType.intrinsicManaAbility)
      }
    }
  }
}
