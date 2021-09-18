package mtg.templates

import mtg.abilities.builder.EffectBuilder._
import mtg.cards.patterns.Spell
import mtg.characteristics.types.Type
import mtg.data.cards.Plains
import mtg.game.turns.StartNextTurnAction
import mtg.helpers.SpecWithTestCards
import mtg.parts.costs.ManaCost
import mtg.stack.adding.ModeChoice

class ModalSpec extends SpecWithTestCards {
  object TestCard extends Spell(
    "Modal Card",
    ManaCost(1),
    Type.Instant,
    Nil,
    chooseOne(
      you.gain(3).life,
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

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.activateAbility(playerOne, Plains)
      manager.castSpell(playerOne, TestCard)

      manager.currentAction must beAnInstanceOf[ModeChoice]
    }

    "not require a target if chosen mode has no targets" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCard)
        .setBattlefield(playerOne, Plains)

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.activateAbility(playerOne, Plains)
      manager.castSpell(playerOne, TestCard)
      manager.chooseMode(playerOne, 0)

      manager.currentAction must bePriorityChoice.forPlayer(playerOne)
    }

    "require a target if chosen mode has targets" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCard)
        .setBattlefield(playerOne, Plains)

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.activateAbility(playerOne, Plains)
      manager.castSpell(playerOne, TestCard)
      manager.chooseMode(playerOne, 1)

      manager.currentAction must beTargetChoice.forPlayer(playerOne)
    }

    "correctly resolve a mode with no targets" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TestCard)
        .setBattlefield(playerOne, Plains)

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
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

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.activateAbility(playerOne, Plains)
      manager.castSpell(playerOne, TestCard)
      manager.chooseMode(playerOne, 1)
      manager.choosePlayer(playerOne, playerTwo)
      manager.resolveNext()

      playerTwo.lifeTotal(manager.gameState) mustEqual playerTwo.lifeTotal(initialState) - 3
    }
  }
}