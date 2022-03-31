package mtg.sets.strixhaven.abilities

import mtg.core.types.SpellType.Lesson
import mtg.game.turns.TurnPhase
import mtg.instructions.verbs.DrawACard
import mtg.sets.alpha.cards.Plains
import mtg.{SpecWithGameStateManager, TestCardCreation}

class LearnSpec extends SpecWithGameStateManager with TestCardCreation {
  val LearnCard = simpleSorcerySpell(Learn)
  val LessonCard = simpleSorcerySpell(Seq(Lesson), DrawACard)

  "learn ability" should {
    "offer the choice of a lesson from the sideboard" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, LearnCard)
        .setSideboard(playerOne, LessonCard, Plains)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, LearnCard)
      manager.resolveNext()
      manager.gameState.currentChoice must beSome(beInstructionChoice[LearnChoice]((_: LearnChoice).possibleLessons must contain(exactly(getCardPrinting(LessonCard).id))))
    }
  }

}
