package mtg.effects.targets

import mtg.core.ObjectOrPlayerId
import mtg.effects.continuous.TargetPreventionEffect
import mtg.effects.filters.Filter
import mtg.effects.identifiers.SingleIdentifier
import mtg.effects.{EffectContext, StackObjectResolutionContext}
import mtg.game.state.{GameState, StackObjectWithState}
import mtg.text.{GrammaticalNumber, NounPhrase}

import scala.reflect.ClassTag

class TargetIdentifier[T <: ObjectOrPlayerId : ClassTag](filter: Filter[T]) extends SingleIdentifier[T] {
  override def getNounPhrase(cardName: String): NounPhrase = {
    NounPhrase.Templated(
      filter.getNounPhraseTemplate(cardName).withPrefix("target"),
      GrammaticalNumber.Singular)
  }

  override def get(gameState: GameState, resolutionContext: StackObjectResolutionContext): (T, StackObjectResolutionContext) = {
    resolutionContext.popTarget.mapLeft(_.asInstanceOf[T])
  }
  def getValidChoices(source: StackObjectWithState, gameState: GameState, effectContext: EffectContext): Seq[ObjectOrPlayerId] = {
    (gameState.gameObjectState.allObjects.map(_.objectId) ++ gameState.gameData.playersInTurnOrder)
      .filter(isValidTarget(source, _, gameState, effectContext))
      .toSeq
  }
  def isValidTarget(source: StackObjectWithState, possibleTarget: ObjectOrPlayerId, gameState: GameState, effectContext: EffectContext): Boolean = {
    possibleTarget.asOptionalInstanceOf[T].exists(filter.matches(_, effectContext, gameState)) &&
      !gameState.gameObjectState.activeContinuousEffects.ofType[TargetPreventionEffect].exists(_.preventsTarget(source, possibleTarget, gameState))
  }
}

object TargetIdentifier {
  def getAll(stackObjectWithState: StackObjectWithState): Seq[TargetIdentifier[_]] = {
    stackObjectWithState.applicableEffectParagraphs.flatMap(_.effects).flatMap(_.targetIdentifiers)
  }
}
