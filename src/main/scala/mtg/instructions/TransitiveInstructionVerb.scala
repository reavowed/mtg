package mtg.instructions

import mtg.effects.StackObjectResolutionContext
import mtg.game.state.GameState
import mtg.instructions.nounPhrases.SingleIdentifyingNounPhrase

trait TransitiveInstructionVerb[SubjectType, ObjectType] extends Verb {
  def postObjectText: Option[String] = None
  def apply(objectPhrase: SingleIdentifyingNounPhrase[ObjectType]): IntransitiveInstructionVerb[SubjectType] = {
    TransitiveInstructionVerbWithObject(this, objectPhrase)
  }
  def resolve(subject: SubjectType, obj: ObjectType, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult
}

case class TransitiveInstructionVerbWithObject[SubjectType, ObjectType](
    transitiveVerbInstruction: TransitiveInstructionVerb[SubjectType, ObjectType],
    objectPhrase: SingleIdentifyingNounPhrase[ObjectType])
  extends IntransitiveInstructionVerb[SubjectType]
{
  override def inflect(verbInflection: VerbInflection, cardName: String): String = {
    val mainText = transitiveVerbInstruction.inflect(verbInflection, cardName) + " " + objectPhrase.getText(cardName)
    transitiveVerbInstruction.postObjectText match {
      case Some(postObjectText) =>
        mainText + " " + postObjectText
      case None =>
        mainText
    }
  }
  override def resolve(subject: SubjectType, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val (objectId, contextAfterObjects) = objectPhrase.identifySingle(gameState, resolutionContext)
    transitiveVerbInstruction.resolve(subject, objectId, gameState, contextAfterObjects)
  }
}
