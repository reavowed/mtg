package mtg.game.actions

import mtg.SpecWithGameStateManager
import mtg.characteristics.Color
import mtg.data.cards.strixhaven.AgelessGuardian
import mtg.data.cards.{Forest, Plains}
import mtg.data.sets.Strixhaven
import mtg.game.actions.cast.CastSpellAction
import mtg.game.objects.GameObject
import mtg.game.turns.StartNextTurnAction
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.priority.PriorityChoice
import org.specs2.matcher.Matcher

class CastSpellSpec extends SpecWithGameStateManager {
  "cast spell action" should {
    "be available for a creature card in hand at sorcery speed" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, Seq(Plains, Forest, AgelessGuardian))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      manager.currentAction must bePriorityChoice.forPlayer(playerOne).withAvailableSpell(AgelessGuardian)
    }

    "not be available for a creature card in hand in upkeep" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, Seq(Plains, Forest, AgelessGuardian))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.currentAction must bePriorityChoice.forPlayer(playerOne).withNoAvailableSpells
    }

    "not be available for a creature card in hand if there is something on the stack" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, Seq(AgelessGuardian, AgelessGuardian))
        .setBattlefield(playerOne, Seq(Plains, Plains))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      // Tap mana and cast first spell
      manager.activateFirstAbility(playerOne, Plains)
      manager.activateFirstAbility(playerOne, Plains)
      manager.castFirstSpell(playerOne, AgelessGuardian)

      manager.currentAction must bePriorityChoice.forPlayer(playerOne).withNoAvailableSpells
    }

    "not be available for a creature card for the non-active player" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerTwo, Seq(Plains, Forest, AgelessGuardian))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.passPriority(playerOne)

      manager.currentAction must bePriorityChoice.forPlayer(playerTwo).withNoAvailableSpells
    }

    // TODO: Can't cast Dryad Arbor
  }

  "casting a vanilla creature" should {
    "do nothing with no mana in pool" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(AgelessGuardian))
        .setBattlefield(playerOne, Seq(Plains, Plains))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      val stateBeforeSpell = manager.currentGameState
      manager.handleDecision(
        manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[CastSpellAction].head.optionText,
        playerOne)

      manager.currentGameState mustEqual stateBeforeSpell
    }

    "move the card to the stack with correct mana in pool" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(AgelessGuardian))
        .setBattlefield(playerOne, Seq(Plains, Plains))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      // Add necessary mana
      manager.handleDecision(
        manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction].head.optionText,
        playerOne)
      manager.handleDecision(
        manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction].head.optionText,
        playerOne)
      manager.currentGameState.gameObjectState.manaPools(playerOne).map(_.manaType) must contain(exactly(Color.White.manaType, Color.White.manaType))

      // Cast spell
      manager.handleDecision(
        manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[CastSpellAction].head.optionText,
        playerOne)

      manager.currentGameState.gameObjectState.manaPools(playerOne) must beEmpty
      manager.currentGameState.gameObjectState.hands(playerOne) must beEmpty
      manager.currentGameState.gameObjectState.stack must contain(exactly(beCardObject(AgelessGuardian)))
    }

    "move the card to the battlefield after all players pass" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(AgelessGuardian))
        .setBattlefield(playerOne, Seq(Plains, Plains))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      // Add necessary mana
      manager.handleDecision(
        manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction].head.optionText,
        playerOne)
      manager.handleDecision(
        manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[ActivateAbilityAction].head.optionText,
        playerOne)
      manager.currentGameState.gameObjectState.manaPools(playerOne).map(_.manaType) must contain(exactly(Color.White.manaType, Color.White.manaType))

      // Cast spell
      manager.handleDecision(
        manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[CastSpellAction].head.optionText,
        playerOne)

      // Resolve the spell
      manager.passPriority(playerOne)
      manager.passPriority(playerTwo)

      manager.currentGameState.gameObjectState.stack must beEmpty
      manager.currentGameState.gameObjectState.battlefield must contain(beCardObject(AgelessGuardian))
    }
  }
}
