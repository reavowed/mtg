package mtg.game.actions

import mtg.data.cards.Plains
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.{SpecWithGameStateManager, TestCardCreation}

class CastSpellTimingSpec extends SpecWithGameStateManager with TestCardCreation {
  val Creature = vanillaCreature(1, 1)
  val Sorcery = simpleSorcerySpell(Nil)

  "cast spell action" should {
    "be available for a creature card in hand at sorcery speed" in {
      val initialState = emptyGameObjectState.setHand(playerOne, Creature)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne).withAvailableSpell(Creature))
    }

    "not be available for a creature card in hand in upkeep" in {
      val initialState = emptyGameObjectState.setHand(playerOne, Creature)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne).withNoAvailableSpells)
    }

    "not be available for a creature card in hand if there is something on the stack" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Creature, Sorcery)
        .setBattlefield(playerOne, Plains, Plains)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      // Tap mana and cast first spell
      manager.castFirstSpell(playerOne, Sorcery)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne).withNoAvailableSpells)
    }

    "not be available for a creature card for the non-active player" in {
      val initialState = emptyGameObjectState.setHand(playerTwo, Creature)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.passPriority(playerOne)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerTwo).withNoAvailableSpells)
    }

    // TODO: Can't cast Dryad Arbor
  }
}
