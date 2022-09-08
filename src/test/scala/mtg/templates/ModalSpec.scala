package mtg.templates

import mtg.SpecWithGameStateManager
import mtg.abilities.builder.InstructionBuilder._
import mtg.cards.patterns.SpellCard
import mtg.definitions.types.Type
import mtg.instructions.nounPhrases
import mtg.instructions.nounPhrases.{AnyTarget, CardName}
import mtg.instructions.verbs.{DealDamage, GainLife}
import mtg.parts.costs.ManaCost
import mtg.sets.alpha.cards.Plains
import mtg.stack.adding.ModeChoice

class ModalSpec extends SpecWithGameStateManager {
  object TestCard extends SpellCard(
    "Modal Card",
    ManaCost(1),
    Type.Instant,
    Nil,
    chooseOne(
      nounPhrases.You(GainLife(3)),
      CardName(DealDamage(3)(AnyTarget))))

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
