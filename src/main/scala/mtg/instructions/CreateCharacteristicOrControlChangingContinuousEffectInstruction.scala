package mtg.instructions

import mtg.actions.CreateContinousEffectsAction
import mtg.core.ObjectId
import mtg.effects.StackObjectResolutionContext
import mtg.effects.condition.Condition
import mtg.game.state.GameState
import mtg.instructions.descriptions.CharacteristicOrControlChangingContinuousEffectDescription
import mtg.instructions.nouns.SetIdentifyingNounPhrase
import mtg.text.VerbInflection
import mtg.utils.TextUtils._

case class CreateCharacteristicOrControlChangingContinuousEffectInstruction(
    objectIdentifier: SetIdentifyingNounPhrase[ObjectId],
    effectDescriptions: Seq[CharacteristicOrControlChangingContinuousEffectDescription],
    condition: Condition)
  extends Instruction
{
  override def getText(cardName: String): String = {
    Seq(
      objectIdentifier.getText(cardName),
      effectDescriptions.map(_.inflect(VerbInflection.Present(objectIdentifier.person, objectIdentifier.number), cardName)).toCommaList("and"),
      "until",
      condition.getText(cardName)
    ).mkString(" ")
  }

  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val (objectIds, finalContext) = objectIdentifier.identifyAll(gameState, resolutionContext)
    val effects = for {
      objectId <- objectIds
      effectDescription <- effectDescriptions
    } yield effectDescription.getEffect(objectId)
    (CreateContinousEffectsAction(effects, resolutionContext, condition), finalContext)
  }
}
