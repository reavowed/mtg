package mtg.game.actions

import mtg.cards.patterns.{CreatureCard, SpellCard}
import mtg.core.types.Type
import mtg.data.cards.Plains
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.helpers.SpecWithTestCards
import mtg.parts.costs.ManaCost

class CastSpellTimingSpec extends SpecWithTestCards {
  val Creature = new CreatureCard("Creature", ManaCost(0), Nil, Nil, (1, 1))
  val Sorcery = new SpellCard("Sorcery", ManaCost(0), Type.Sorcery, Nil, Nil)
  override def testCards = Seq(Creature, Sorcery)

  "cast spell action" should {
    "be available for a creature card in hand at sorcery speed" in {
      val initialState = emptyGameObjectState.setHand(playerOne, Seq(Creature))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne).withAvailableSpell(Creature))
    }

    "not be available for a creature card in hand in upkeep" in {
      val initialState = emptyGameObjectState.setHand(playerOne, Seq(Creature))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne).withNoAvailableSpells)
    }

    "not be available for a creature card in hand if there is something on the stack" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(Creature, Sorcery))
        .setBattlefield(playerOne, Seq(Plains, Plains))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      // Tap mana and cast first spell
      manager.castFirstSpell(playerOne, Sorcery)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne).withNoAvailableSpells)
    }

    "not be available for a creature card for the non-active player" in {
      val initialState = emptyGameObjectState.setHand(playerTwo, Seq(Creature))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.passPriority(playerOne)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerTwo).withNoAvailableSpells)
    }

    // TODO: Can't cast Dryad Arbor
  }
}
