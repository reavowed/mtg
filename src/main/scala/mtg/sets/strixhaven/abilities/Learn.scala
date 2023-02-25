package mtg.sets.strixhaven.abilities

import mtg.actions.moveZone.MoveToHandAction
import mtg.actions.{DiscardCardAction, DrawCardAction, RevealAction}
import mtg.definitions.types.SpellType.Lesson
import mtg.definitions.{ObjectId, PlayerId}
import mtg.game.state.{Choice, CurrentCharacteristics, GameAction, GameState}
import mtg.instructions.{Instruction, InstructionAction}

/**
 * 701.45. Learn
 * 701.45a. "Learn" means "You may discard a card. If you do, draw a card. If you didn't discard a card, you may reveal
 *          a Lesson card you own from outside the game and put it into your hand."
 */
object Learn extends Instruction {
  override def getText(cardName: String): String = "learn"
  override def resolve: InstructionAction = InstructionAction.withoutContextUpdate { (resolutionContext, gameState) =>
    val player = resolutionContext.youPlayerId
    LearnChoice(
      player,
      gameState.gameObjectState.sideboards(player)
        .map(_.objectId)
        .filter(id => CurrentCharacteristics.getCharacteristics(id, gameState).subtypes.contains(Lesson))
    ).flatMap(identity)
  }
}

case class LearnChoice(playerToAct: PlayerId, possibleLessons: Seq[ObjectId]) extends Choice[GameAction[Any]] {
  override def handleDecision(
    serializedDecision: String)(
    implicit gameState: GameState
  ): Option[GameAction[Any]] = {
    def getLesson(lessonId: ObjectId): GameAction[Any] = {
      Seq(RevealAction(playerToAct, lessonId), MoveToHandAction(lessonId)).traverse
    }
    def loot(cardId: ObjectId): GameAction[Any] = {
      Seq(DiscardCardAction(playerToAct, cardId), DrawCardAction(playerToAct)).traverse
    }
    possibleLessons.find(_.toString == serializedDecision).map(getLesson) orElse
      gameState.gameObjectState.hands(playerToAct).map(_.objectId).find(_.toString == serializedDecision).map(loot) orElse
      Option(serializedDecision).filter(_ == "Decline").map(_ => ())
  }
}
