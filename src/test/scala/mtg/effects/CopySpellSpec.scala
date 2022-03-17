package mtg.effects

import mtg.abilities.builder.InstructionBuilder._
import mtg.cards.patterns.Spell
import mtg.core.types.Type
import mtg.game.objects.CopyOfSpell
import mtg.game.turns.TurnPhase
import mtg.helpers.SpecWithTestCards
import mtg.instructions.actions.{Copy, GainLife}
import mtg.parts.costs.ManaCost

class CopySpellSpec extends SpecWithTestCards {
  object TestCopyCard extends Spell(
    "Copy Card",
    ManaCost(0),
    Type.Instant,
    Nil,
    Copy(target(spell)))
  object TestGainOneLifeCard extends Spell(
    "Lifegain Card",
    ManaCost(0),
    Type.Instant,
    Nil,
    you(GainLife(1)))

  override def testCards = Seq(TestCopyCard, TestGainOneLifeCard)

  "copying a spell" should {
    "duplicate a simple spell" in {
      val initialState = emptyGameObjectState.setHand(playerOne, Seq(TestCopyCard, TestGainOneLifeCard))

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
      val initialState = emptyGameObjectState.setHand(playerOne, Seq(TestCopyCard, TestGainOneLifeCard))

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
