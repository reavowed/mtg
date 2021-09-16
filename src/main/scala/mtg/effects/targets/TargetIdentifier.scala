package mtg.effects.targets

import mtg.effects.continuous.TargetPreventionEffect
import mtg.effects.filters.Filter
import mtg.effects.identifiers.Identifier
import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.game.ObjectOrPlayer
import mtg.game.state.{GameState, StackObjectWithState}

import scala.reflect.ClassTag

class TargetIdentifier[T <: ObjectOrPlayer : ClassTag](filter: Filter[T]) extends Identifier[T] {
  def getText(cardName: String): String = s"target ${filter.getText(cardName)}"

  def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext) = {
    resolutionContext.popTarget.mapLeft(_.asInstanceOf[T])
  }
  def getValidChoices(source: StackObjectWithState, gameState: GameState, effectContext: EffectContext): Seq[ObjectOrPlayer] = {
    (gameState.gameObjectState.allObjects.map(_.objectId) ++ gameState.gameData.playersInTurnOrder)
      .filter(isValidTarget(source, _, gameState, effectContext))
      .toSeq
  }
  def isValidTarget(source: StackObjectWithState, possibleTarget: ObjectOrPlayer, gameState: GameState, effectContext: EffectContext): Boolean = {
    possibleTarget.asOptionalInstanceOf[T].exists(filter.isValid(_, effectContext, gameState)) &&
      !gameState.gameObjectState.activeContinuousEffects.ofType[TargetPreventionEffect].exists(_.preventsTarget(source, possibleTarget, gameState))
  }
}

object TargetIdentifier {
  def getAll(stackObjectWithState: StackObjectWithState): Seq[TargetIdentifier[_]] = {
    stackObjectWithState.applicableEffectParagraphs.flatMap(_.effects).flatMap(_.targetIdentifiers)
  }
}
