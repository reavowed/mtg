package mtg.instructions.nounPhrases

import mtg.continuousEffects.TargetPreventionEffect
import mtg.core.{ObjectId, ObjectOrPlayerId}
import mtg.effects.filters.Filter
import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.game.state.{GameState, StackObjectWithState}
import mtg.text.VerbPerson

import scala.reflect.ClassTag

class Target[T <: ObjectOrPlayerId : ClassTag](filter: Filter[T]) extends SingleIdentifyingNounPhrase[T] {
  override def getText(cardName: String): String = {
    "target " + filter.getSingular(cardName)
  }
  override def person: VerbPerson = VerbPerson.Third

  override def identifySingle(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext) = {
    resolutionContext.popTarget.mapLeft(_.asInstanceOf[T])
  }
  def getValidChoices(source: StackObjectWithState, gameState: GameState, effectContext: EffectContext): Seq[ObjectOrPlayerId] = {
    (gameState.gameObjectState.allObjects.map(_.objectId) ++ gameState.gameData.playersInTurnOrder)
      .filter(isValidTarget(source, _, gameState, effectContext))
      .toSeq
  }
  def isValidTarget(source: StackObjectWithState, possibleTarget: ObjectOrPlayerId, gameState: GameState, effectContext: EffectContext): Boolean = {
    possibleTarget.asOptionalInstanceOf[T].exists((t: T) => filter.describes(t, gameState, effectContext)) &&
      !gameState.gameObjectState.activeContinuousEffects.ofType[TargetPreventionEffect].exists(_.preventsTarget(source, possibleTarget, gameState))
  }
}

object Target {
  def apply(filter: Filter[ObjectId]): Target[ObjectId] = new Target(filter)
  def getAll(stackObjectWithState: StackObjectWithState): Seq[Target[_]] = {
    stackObjectWithState.applicableEffectParagraphs.flatMap(_.instructions).flatMap(_.targetIdentifiers)
  }
}
