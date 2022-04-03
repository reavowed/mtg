package mtg.sets.strixhaven.abilities

import mtg.core.types.SpellType.Lesson
import mtg.core.zones.Zone
import mtg.game.state.history.LogEvent.RevealCard
import mtg.game.turns.TurnPhase
import mtg.instructions.verbs.DrawACard
import mtg.sets.alpha.cards.{Forest, Plains}
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

      manager.gameState.currentChoice must beSome(beInstructionChoice[LearnChoice]((_: LearnChoice).possibleLessons must
        contain(exactly(manager.getCard(LessonCard).objectId))))
    }

    "reveal chosen lesson" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, LearnCard)
        .setSideboard(playerOne, LessonCard, Plains)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, LearnCard)
      manager.resolveNext()
      manager.chooseCard(playerOne, LessonCard)

      manager.gameState.gameHistory.logEvents.map(_.logEvent) must contain(RevealCard(playerOne, LessonCard.name))
    }

    "move chosen lesson to hand" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, LearnCard)
        .setSideboard(playerOne, LessonCard, Plains)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, LearnCard)
      manager.resolveNext()
      manager.chooseCard(playerOne, LessonCard)

      manager.gameState.gameObjectState.sideboards(playerOne) must not contain(beCardObject(LessonCard))
      manager.gameState.gameObjectState.hands(playerOne) must contain(beCardObject(LessonCard))
    }

    "allow discarding a card from hand to draw" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, LearnCard, Plains)
        .setLibrary(playerOne, Forest)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, LearnCard)
      manager.resolveNext()
      manager.chooseCard(playerOne, Plains)

      manager.gameState.gameObjectState.hands(playerOne) must not contain(beCardObject(Plains))
      manager.gameState.gameObjectState.graveyards(playerOne) must contain(beCardObject(Plains))
      manager.gameState.gameObjectState.hands(playerOne) must contain(beCardObject(Forest))
    }
  }

}
