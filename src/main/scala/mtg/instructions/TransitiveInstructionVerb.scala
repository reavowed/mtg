package mtg.instructions

import mtg.effects.StackObjectResolutionContext
import mtg.effects.identifiers.SingleIdentifier
import mtg.game.state.GameState
import mtg.text.{Verb, VerbInflection}

trait TransitiveInstructionVerb[SubjectType, ObjectType] extends Verb {
  def postObjectText: Option[String] = None
  def apply(objectIdentifier: SingleIdentifier[ObjectType]): IntransitiveInstructionVerb[SubjectType] = {
    TransitiveInstructionVerbWithObject(this, objectIdentifier)
  }
  def resolve(subject: SubjectType, obj: ObjectType, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult
}

case class TransitiveInstructionVerbWithObject[SubjectType, ObjectType](
    transitiveVerbInstruction: TransitiveInstructionVerb[SubjectType, ObjectType],
    objectIdentifier: SingleIdentifier[ObjectType])
  extends IntransitiveInstructionVerb[SubjectType]
{
  override def inflect(verbInflection: VerbInflection, cardName: String): String = {
    val mainText = transitiveVerbInstruction.inflect(verbInflection, cardName) + " " + objectIdentifier.getText(cardName)
    transitiveVerbInstruction.postObjectText match {
      case Some(postObjectText) =>
        mainText + " " + postObjectText
      case None =>
        mainText
    }
  }
  override def resolve(subject: SubjectType, gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val (objectId, contextAfterObjects) = objectIdentifier.identifySingle(gameState, resolutionContext)
    transitiveVerbInstruction.resolve(subject, objectId, gameState, contextAfterObjects)
  }
}
