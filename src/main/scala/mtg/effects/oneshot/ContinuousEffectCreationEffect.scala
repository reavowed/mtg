package mtg.effects.oneshot

import mtg.effects.condition.ConditionDefinition
import mtg.effects.identifiers.{MultipleIdentifier, SingleIdentifier}
import mtg.effects.oneshot.descriptions.ContinuousEffectDescription
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.events.CreateContinousEffects
import mtg.game.ObjectId
import mtg.game.state.GameState
import mtg.utils.TextUtils.StringSeqExtensions

case class ContinuousEffectCreationEffect(
    objectIdentifier: MultipleIdentifier[ObjectId],
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
    val (objectIds, finalContext) = objectIdentifier.getAll(gameState, resolutionContext)
    val condition = conditionDefinition.getCondition(gameState, finalContext)
    val effects = for {
      objectId <- objectIds
      effectDescription <- effectDescriptions
    } yield effectDescription.getEffect(objectId)
    (CreateContinousEffects(effects, condition), finalContext)
  }
}
