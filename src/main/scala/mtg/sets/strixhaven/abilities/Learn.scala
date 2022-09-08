package mtg.sets.strixhaven.abilities

import mtg.actions.moveZone.MoveToHandAction
import mtg.actions.{DiscardCardAction, DrawCardAction, RevealAction}
import mtg.definitions.types.SpellType.Lesson
import mtg.definitions.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.{CurrentCharacteristics, GameState}
import mtg.instructions.{Instruction, InstructionChoice, InstructionResult}

/**
 * 701.45. Learn
 * 701.45a. "Learn" means "You may discard a card. If you do, draw a card. If you didn't discard a card, you may reveal
 *          a Lesson card you own from outside the game and put it into your hand."
 */
object Learn extends Instruction {
  override def getText(cardName: String): String = "learn"
  override def resolve(gameState: GameState, resolutionContext: InstructionResolutionContext): InstructionResult = {
    val player = resolutionContext.youPlayerId
    LearnChoice(
      player,
      gameState.gameObjectState.sideboards(player)
        .map(_.objectId)
        .filter(id => CurrentCharacteristics.getCharacteristics(id, gameState).subtypes.contains(Lesson)))
  }
}

case class LearnChoice(playerChoosing: PlayerId, possibleLessons: Seq[ObjectId]) extends InstructionChoice {
  override def parseDecision(
    serializedDecision: String,
    resolutionContext: InstructionResolutionContext)(
    implicit gameState: GameState
  ): Option[InstructionResult] = {
    def getLesson(lessonId: ObjectId): InstructionResult = {
      (Seq(RevealAction(resolutionContext.youPlayerId, lessonId), MoveToHandAction(lessonId)).traverse, resolutionContext)
    }
    def loot(cardId: ObjectId): InstructionResult = {
      (Seq(DiscardCardAction(playerChoosing, cardId), DrawCardAction(playerChoosing)).traverse, resolutionContext)
    }
    possibleLessons.find(_.toString == serializedDecision).map(getLesson) orElse
      gameState.gameObjectState.hands(playerChoosing).map(_.objectId).find(_.toString == serializedDecision).map(loot) orElse
      Option(serializedDecision).filter(_ == "Decline").map(_ => resolutionContext)
  }
}
