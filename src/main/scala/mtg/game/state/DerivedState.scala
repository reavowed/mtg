package mtg.game.state

import mtg._
import mtg.abilities.{AbilityDefinition, StaticAbility, TriggeredAbility, TriggeredAbilityDefinition}
import mtg.characteristics.types.BasicLandType
import mtg.characteristics.types.Type.Creature
import mtg.effects.ContinuousEffect
import mtg.effects.continuous.{AddAbilityEffect, ModifyPowerToughnessEffect}
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
}

object DerivedState {
  private def getActiveAbilitiesOfType[T <: AbilityDefinition : ClassTag](objectsWithState: View[ObjectWithState]): View[(T, ObjectWithState)] = {
    objectsWithState.flatMap(objectWithState => objectWithState.characteristics.abilities.ofType[T].filter(_.isFunctional(objectWithState)).map(_ -> objectWithState))
  }
  def getActiveTriggeredAbilities(objectsWithState: View[ObjectWithState]): View[TriggeredAbility] = {
    getActiveAbilitiesOfType[TriggeredAbilityDefinition](objectsWithState)
      .map { case (abilityDefinition, objectWithState) => TriggeredAbility(abilityDefinition, objectWithState.gameObject.objectId, objectWithState.controllerOrOwner) }
  }
  def getActiveContinuousEffectsFromStaticAbilities(objectsWithState: View[ObjectWithState]): View[ContinuousEffect] = {
    getActiveAbilitiesOfType[StaticAbility](objectsWithState)
      .flatMap { case (ability, objectWithState) => ability.getEffects(objectWithState) }
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
      applyPowerAndToughnessModifiers(gameObjectState, _)
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

  def applyPowerAndToughnessModifiers(gameObjectState: GameObjectState, objectStates: View[ObjectWithState]): View[ObjectWithState] = {
    val effects = getActiveContinuousEffects(gameObjectState, objectStates).ofType[ModifyPowerToughnessEffect].toSeq
    objectStates
      .map { objectWithState => {
        val modifiers = effects.filter(_.affectedObject == objectWithState.gameObject.objectId).map(_.powerToughnessModifier) ++
          (if (objectWithState.characteristics.types.contains(Creature))
            objectWithState.gameObject.counters.view.ofLeftType[PowerToughnessModifyingCounter]
              .map { case (counterType, number) => counterType.modifier * number}
          else Nil)
        modifiers.foldLeft(objectWithState)((o, m) => o.updateCharacteristics(m.applyToCharacteristics))
      }}
  }
}
