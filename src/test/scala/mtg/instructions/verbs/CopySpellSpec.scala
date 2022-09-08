package mtg.instructions.verbs

import mtg.game.objects.CopyOfSpell
import mtg.game.turns.TurnPhase
import mtg.instructions.nounPhrases.{Target, You}
import mtg.instructions.nouns.Spell
import mtg.{SpecWithGameStateManager, TestCardCreation}

class CopySpellSpec extends SpecWithGameStateManager with TestCardCreation {
  val TestCopyCard = simpleInstantSpell(Copy(Target(Spell)))
  val TestGainOneLifeCard = simpleInstantSpell(You(GainLife(1)))

  "copying a spell" should {
    "duplicate a simple spell" in {
      val initialState = emptyGameObjectState.setHand(playerOne, TestCopyCard, TestGainOneLifeCard)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestGainOneLifeCard)
      manager.castSpell(playerOne, TestCopyCard)
      manager.chooseCard(playerOne, TestGainOneLifeCard)
      manager.resolveNext()

      manager.gameState.gameObjectState.stack.size mustEqual 2
      manager.gameState.gameObjectState.stack(1).underlyingObject must beAnInstanceOf[CopyOfSpell]
      manager.gameState.gameObjectState.stack(1).underlyingObject.baseCharacteristics.name must beSome(TestGainOneLifeCard.name)
    }
  }

  "a copied spell" should {
    "have the same effects" in {
      val initialState = emptyGameObjectState.setHand(playerOne, TestCopyCard, TestGainOneLifeCard)

      val initialLifeTotal = initialState.lifeTotals(playerOne)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TestGainOneLifeCard)
      manager.castSpell(playerOne, TestCopyCard)
      manager.chooseCard(playerOne, TestGainOneLifeCard)
      manager.passUntilStackEmpty()

      manager.gameState.gameObjectState.lifeTotals(playerOne) mustEqual initialLifeTotal + 2
    }
  }
}
