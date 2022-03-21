package mtg.templates

import mtg.abilities.builder.InstructionBuilder._
import mtg.cards.patterns.SpellCard
import mtg.core.types.Type
import mtg.data.cards.Plains
import mtg.helpers.SpecWithTestCards
import mtg.instructions.actions.GainLife
import mtg.instructions.nouns.You
import mtg.parts.costs.ManaCost
import mtg.stack.adding.ModeChoice

class ModalSpec extends SpecWithTestCards {
  object TestCard extends SpellCard(
    "Modal Card",
    ManaCost(1),
    Type.Instant,
    Nil,
    chooseOne(
      You(GainLife(3)),
      cardName.deals(3).damageTo(anyTarget))
  )

  override def testCards = Seq(TestCard)

  "modal card" should {
    "have correct oracle text" in {
      TestCard.text mustEqual
        s"""Choose one —
          |• You gain 3 life.
          |• ${TestCard.name} deals 3 damage to any target.""".stripMargin.replace("\r", "")
    }

    "require a choice of mode on casting" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCard)
        .setBattlefield(playerOne, Plains)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.activateAbility(playerOne, Plains)
      manager.castSpell(playerOne, TestCard)

      manager.currentChoice must beSome(beAnInstanceOf[ModeChoice])
    }

    "not require a target if chosen mode has no targets" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCard)
        .setBattlefield(playerOne, Plains)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.activateAbility(playerOne, Plains)
      manager.castSpell(playerOne, TestCard)
      manager.chooseMode(playerOne, 0)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne))
    }

    "require a target if chosen mode has targets" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCard)
        .setBattlefield(playerOne, Plains)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.activateAbility(playerOne, Plains)
      manager.castSpell(playerOne, TestCard)
      manager.chooseMode(playerOne, 1)

      manager.currentChoice must beSome(beTargetChoice.forPlayer(playerOne))
    }

    "correctly resolve a mode with no targets" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCard)
        .setBattlefield(playerOne, Plains)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.activateAbility(playerOne, Plains)
      manager.castSpell(playerOne, TestCard)
      manager.chooseMode(playerOne, 0)
      manager.resolveNext()

      playerOne.lifeTotal(manager.gameState) mustEqual playerOne.lifeTotal(initialState) + 3
    }

    "correctly resolve a mode with a target" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCard)
        .setBattlefield(playerOne, Plains)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.activateAbility(playerOne, Plains)
      manager.castSpell(playerOne, TestCard)
      manager.chooseMode(playerOne, 1)
      manager.choosePlayer(playerOne, playerTwo)
      manager.resolveNext()

      playerTwo.lifeTotal(manager.gameState) mustEqual playerTwo.lifeTotal(initialState) - 3
    }
  }
}
