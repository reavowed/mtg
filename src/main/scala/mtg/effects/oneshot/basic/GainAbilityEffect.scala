package mtg.effects.oneshot.basic

import mtg.abilities.AbilityDefinition
import mtg.effects.OneShotEffect
import mtg.effects.condition.ConditionDefinition
import mtg.effects.continuous.AddAbilityEffect
import mtg.effects.identifiers.Identifier
import mtg.effects.oneshot.{OneShotEffectResolutionContext, OneShotEffectResult}
import mtg.events.CreateContinousEffect
import mtg.game.ObjectId
import mtg.game.state.GameState

case class GainAbilityEffect(objectIdentifier: Identifier[ObjectId], ability: AbilityDefinition, conditionDefinition: ConditionDefinition) extends OneShotEffect {
  override def getText(cardName: String): String = objectIdentifier.getText(cardName) + " gains " + ability.getQuotedDescription(cardName) + " until " + conditionDefinition.getText(cardName)
  override def resolve(gameState: GameState, resolutionContext: OneShotEffectResolutionContext): OneShotEffectResult = {
    val (affectedObject, contextAfterObject) = objectIdentifier.get(gameState, resolutionContext)
    val condition = conditionDefinition.getCondition(gameState, contextAfterObject)
    (CreateContinousEffect(AddAbilityEffect(affectedObject, ability), condition), contextAfterObject)
  }
}
