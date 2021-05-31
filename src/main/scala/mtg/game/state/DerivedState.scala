package mtg.game.state

import mtg._
import mtg.abilities.StaticAbility
import mtg.characteristics.types.BasicLandType
import mtg.characteristics.types.Type.Creature
import mtg.effects.continuous.ContinuousEffect
import mtg.game.ObjectId
import mtg.game.objects.GameObjectState
import mtg.parts.counters.PowerToughnessModifyingCounter

import scala.collection.View
import scala.reflect.ClassTag

case class DerivedState(
    basicStates: Map[ObjectId, BasicObjectWithState],
    permanentStates: Map[ObjectId, PermanentObjectWithState],
    spellStates: Map[ObjectId, StackObjectWithState]
) {
  val allObjectStates: Map[ObjectId, ObjectWithState] = basicStates ++ permanentStates ++ spellStates
  val activeContinuousEffects: Seq[ContinuousEffect] = allObjectStates.values.view
    .flatMap(objectWithState => objectWithState.characteristics.abilities.ofType[StaticAbility].filter(_.isFunctional(objectWithState)).flatMap(_.getEffects(objectWithState)))
    .toSeq
}

object DerivedState {
  def calculateFromGameObjectState(gameObjectState: GameObjectState): DerivedState = {
    val baseStates = gameObjectState.allObjects.map(_.baseState)

    val finalStates = Seq(
      addIntrinsicManaAbilities(_),
      applyPowerAndToughnessCounters(_)
    ).foldLeft(baseStates) { (objectStates, updater) => updater(objectStates) }

    def mapOfType[T <: ObjectWithState : ClassTag] = finalStates.ofType[T].map(objectWithState => objectWithState.gameObject.objectId -> objectWithState).toMap

    DerivedState(mapOfType[BasicObjectWithState], mapOfType[PermanentObjectWithState], mapOfType[StackObjectWithState])
  }

  def addIntrinsicManaAbilities(objectStates: View[ObjectWithState]): View[ObjectWithState] = {
    objectStates.map { objectWithState =>
      objectWithState.characteristics.subTypes.ofType[BasicLandType].foldLeft(objectWithState) { (objectWithState, landType) =>
        objectWithState.addAbility(landType.intrinsicManaAbility)
      }
    }
  }

  def applyPowerAndToughnessCounters(objectStates: View[ObjectWithState]): View[ObjectWithState] = {
    objectStates
      .map { objectWithState =>
        if (objectWithState.characteristics.types.contains(Creature))
          objectWithState.gameObject.counters.view.ofLeftType[PowerToughnessModifyingCounter]
            .foldLeft(objectWithState) { case (obj, (counterType, number)) =>
              obj.updateCharacteristics(c => c.copy(
                power = c.power.map(_ + (counterType.powerModifier * number)),
                toughness = c.toughness.map(_ + (counterType.toughnessModifier * number))))
            }
        else objectWithState
      }
  }
}
