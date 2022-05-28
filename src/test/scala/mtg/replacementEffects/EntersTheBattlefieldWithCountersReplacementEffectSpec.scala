package mtg.replacementEffects

import mtg.game.turns.TurnPhase
import mtg.instructions.nounPhrases.CardName
import mtg.instructions.suffixDescriptors.With
import mtg.instructions.verbs.EntersTheBattlefield
import mtg.parts.counters.PlusOnePlusOneCounter
import mtg.{SpecWithGameStateManager, TestCardCreation}

class EntersTheBattlefieldWithCountersReplacementEffectSpec extends SpecWithGameStateManager with TestCardCreation {
  val TestCreature = zeroManaCreature(
    Seq(CardName(EntersTheBattlefield(With(1, PlusOnePlusOneCounter)))),
    (1, 1))

  "a creature that enters the battlefield with a +1/+1 counter" should {
    "have that counter after it resolves as a spell" in {
      val initialState = emptyGameObjectState.setHand(playerOne, TestCreature)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestCreature)
      manager.resolveNext()

      manager.getState(manager.getCard(TestCreature)).gameObject.counters(PlusOnePlusOneCounter) mustEqual 1
    }
  }

}
