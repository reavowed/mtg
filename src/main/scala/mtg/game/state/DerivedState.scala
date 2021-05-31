package mtg.game.state

import mtg._
import mtg.abilities.StaticAbility
import mtg.characteristics.types.BasicLandType
import mtg.characteristics.types.Type.Creature
import mtg.effects.ContinuousEffect
import mtg.effects.continuous.AddAbilityEffect
import mtg.game.ObjectId
import mtg.game.objects.{FloatingActiveContinuousEffect, GameObjectState}
import mtg.parts.counters.PowerToughnessModifyingCounter

import scala.collection.View
import scala.reflect.ClassTag

case class DerivedState(
    basicStates: Map[ObjectId, BasicObjectWithState],
    permanentStates: Map[ObjectId, PermanentObjectWithState],
    spellStates: Map[ObjectId, StackObjectWithState]
) {
  val allObjectStates: Map[ObjectId, ObjectWithState] = basicStates ++ permanentStates ++ spellStates
}

object DerivedState {
  def getActiveContinuousEffectsFromStaticAbilities(objectsWithState: View[ObjectWithState]): View[ContinuousEffect] = {
    objectsWithState.flatMap(objectWithState => objectWithState.characteristics.abilities.ofType[StaticAbility].filter(_.isFunctional(objectWithState)).flatMap(_.getEffects(objectWithState)))
  }
  def getActiveContinuousEffects(
    gameObjectState: GameObjectState,
    objectsWithState: View[ObjectWithState]
  ): View[ContinuousEffect] = {
    gameObjectState.floatingActiveContinuousEffects.view.map(_.effect) ++ getActiveContinuousEffectsFromStaticAbilities(objectsWithState)
  }

  def calculateFromGameObjectState(gameObjectState: GameObjectState): DerivedState = {
    val baseStates = gameObjectState.allObjects.map(_.baseState)

    val finalStates = Seq(
      addIntrinsicManaAbilities(_),
      addAbilities(gameObjectState, _),
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

  def addAbilities(gameObjectState: GameObjectState, objectStates: View[ObjectWithState]): View[ObjectWithState] = {
    val effects = getActiveContinuousEffects(gameObjectState, objectStates).ofType[AddAbilityEffect].toSeq
    // TODO: timestamps
    objectStates.map { objectWithState =>
      effects.filter(_.affectedObject == objectWithState.gameObject.objectId)
        .foldLeft(objectWithState) { (o, e) => o.addAbility(e.abilityDefinition) }
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
