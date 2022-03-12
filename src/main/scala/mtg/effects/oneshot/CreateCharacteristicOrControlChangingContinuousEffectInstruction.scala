package mtg.effects.oneshot

import mtg.core.ObjectId
import mtg.effects.condition.ConditionDefinition
import mtg.effects.identifiers.MultipleIdentifier
import mtg.effects.oneshot.descriptions.CharacteristicOrControlChangingContinuousEffectDescription
import mtg.effects.{Instruction, StackObjectResolutionContext}
import mtg.actions.CreateContinousEffectsAction
import mtg.game.state.GameState
import mtg.text.{Sentence, VerbPhraseTemplate}

case class CreateCharacteristicOrControlChangingContinuousEffectInstruction(
    objectIdentifier: MultipleIdentifier[ObjectId],
    effectDescriptions: Seq[CharacteristicOrControlChangingContinuousEffectDescription],
    conditionDefinition: ConditionDefinition)
  extends Instruction
{
  override def getText(cardName: String): String = {
    Sentence.NounAndVerb(
      objectIdentifier.getNounPhrase(cardName),
      VerbPhraseTemplate.List(effectDescriptions.map(_.getVerbPhraseTemplate(cardName)))
        .withSuffix("until")
        .withSuffix(conditionDefinition.getText(cardName))
    ).text
  }

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val (objectIds, finalContext) = objectIdentifier.getAll(gameState, resolutionContext)
    val condition = conditionDefinition.getCondition(gameState, finalContext)
    val effects = for {
      objectId <- objectIds
      effectDescription <- effectDescriptions
    } yield effectDescription.getEffect(objectId)
    (CreateContinousEffectsAction(effects, condition), finalContext)
  }
}
