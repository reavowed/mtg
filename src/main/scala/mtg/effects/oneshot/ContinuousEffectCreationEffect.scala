package mtg.effects.oneshot

import mtg.effects.{EffectContext, OneShotEffect, StackObjectResolutionContext}
import mtg.effects.condition.ConditionDefinition
import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.descriptions.ContinuousEffectDescription
import mtg.events.CreateContinousEffects
import mtg.game.ObjectId
import mtg.game.state.GameState
import mtg.utils.TextUtils.StringSeqExtensions

case class ContinuousEffectCreationEffect(
    objectIdentifier: Identifier[ObjectId],
    effectDescriptions: Seq[ContinuousEffectDescription],
    conditionDefinition: ConditionDefinition)
  extends OneShotEffect
{
  override def getText(cardName: String): String = {
    objectIdentifier.getText(cardName) +
      " " +
      effectDescriptions.map(_.getText(cardName)).toCommaList +
      " until " +
      conditionDefinition.getText(cardName)
  }

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
    val (objectId, finalContext) = objectIdentifier.get(gameState, resolutionContext)
    val condition = conditionDefinition.getCondition(gameState, finalContext)
    (CreateContinousEffects(effectDescriptions.map(_.getEffect(objectId)), condition), finalContext)
  }
}
