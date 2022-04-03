package mtg.sets.strixhaven.abilities

import mtg.actions.RevealAction
import mtg.actions.moveZone.MoveToHandAction
import mtg.core.types.SpellType.Lesson
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.StackObjectResolutionContext
import mtg.game.state.{CurrentCharacteristics, GameState, WrappedOldUpdates}
import mtg.instructions.{Instruction, InstructionChoice, InstructionResult}

/**
 * 701.45. Learn
 * 701.45a. "Learn" means "You may discard a card. If you do, draw a card. If you didn't discard a card, you may reveal
 *          a Lesson card you own from outside the game and put it into your hand."
 */
object Learn extends Instruction {
  override def getText(cardName: String): String = "learn"
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val player = resolutionContext.controllingPlayer
    LearnChoice(
      player,
      gameState.gameObjectState.sideboards(player)
        .map(_.objectId)
        .filter(id => CurrentCharacteristics.getCharacteristics(id, gameState).subtypes.contains(Lesson)))
  }
}

case class LearnChoice(playerChoosing: PlayerId, possibleLessons: Seq[ObjectId]) extends InstructionChoice {
  override def parseDecision(serializedDecision: String, resolutionContext: StackObjectResolutionContext): Option[InstructionResult] = {
    for {
      lessonId <- possibleLessons.find(_.toString == serializedDecision)
    } yield {
      (WrappedOldUpdates(RevealAction(resolutionContext.controllingPlayer, lessonId), MoveToHandAction(lessonId)), resolutionContext)
    }
  }
}
