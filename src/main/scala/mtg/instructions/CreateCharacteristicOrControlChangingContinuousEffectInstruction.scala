package mtg.instructions

import mtg.core.ObjectId
import mtg.effects.condition.Condition
import mtg.effects.identifiers.MultipleIdentifier
import mtg.effects.StackObjectResolutionContext
import mtg.actions.CreateContinousEffectsAction
import mtg.game.state.GameState
import mtg.instructions.descriptions.CharacteristicOrControlChangingContinuousEffectDescription
import mtg.text.{Sentence, VerbPhraseTemplate}

case class CreateCharacteristicOrControlChangingContinuousEffectInstruction(
    objectIdentifier: MultipleIdentifier[ObjectId],
    effectDescriptions: Seq[CharacteristicOrControlChangingContinuousEffectDescription],
    condition: Condition)
  extends Instruction
{
  override def getText(cardName: String): String = {
    Sentence.NounAndVerb(
      objectIdentifier.getNounPhrase(cardName),
      VerbPhraseTemplate.List(effectDescriptions.map(_.getVerbPhraseTemplate(cardName)))
        .withSuffix("until")
        .withSuffix(condition.getText(cardName))
    ).text
  }

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val (objectIds, finalContext) = objectIdentifier.getAll(gameState, resolutionContext)
    val effects = for {
      objectId <- objectIds
      effectDescription <- effectDescriptions
    } yield effectDescription.getEffect(objectId)
    (CreateContinousEffectsAction(effects, resolutionContext, condition), finalContext)
  }
}
