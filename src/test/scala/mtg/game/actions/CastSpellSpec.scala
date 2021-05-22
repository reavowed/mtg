package mtg.game.actions

import mtg.SpecWithGameStateManager
import mtg.characteristics.Color
import mtg.data.cards.strixhaven.AgelessGuardian
import mtg.data.cards.{Forest, Plains}
import mtg.data.sets.Strixhaven
import mtg.game.actions.cast.CastSpellAction
import mtg.game.objects.GameObject
import mtg.game.turns.{PriorityChoice, StartNextTurnAction}
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import org.specs2.matcher.Matcher

class CastSpellSpec extends SpecWithGameStateManager {
  def beCastSpellAction(gameObject: GameObject): Matcher[CastSpellAction] = { (castSpellAction: CastSpellAction) =>
    (castSpellAction.objectToCast.gameObject == gameObject, "was given object", "was not given object")
  }
  "cast spell action" should {
    "be available for a creature card in hand at sorcery speed" in {
      val hand = Seq(Plains, Forest, AgelessGuardian).map(Strixhaven.cardPrintingsByDefinition)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, hand)

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      val creatureObject = playerOne.hand(manager.currentGameState).getCard(AgelessGuardian)
      manager.currentGameState.pendingActions.head should bePriorityForPlayer(playerOne)
      manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[CastSpellAction] must contain(exactly(beCastSpellAction(creatureObject)))
    }
    "not be available for a creature card in hand in upkeep" in {
      val hand = Seq(Plains, Forest, AgelessGuardian).map(Strixhaven.cardPrintingsByDefinition)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, hand)

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.currentGameState.pendingActions.head should bePriorityForPlayer(playerOne)
      manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[CastSpellAction] must beEmpty
    }

    // TODO: not for creature card with something on the stack
    // TODO: Can't cast Dryad Arbor
  }

  "casting a vanilla creature" should {
    "do nothing with no mana in pool" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, Seq(Strixhaven.cardPrintingsByDefinition(AgelessGuardian)))
        .setBattlefield(Map(playerOne -> Seq(Plains, Plains).map(Strixhaven.cardPrintingsByDefinition)))

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
        .setHand(playerOne, Seq(Strixhaven.cardPrintingsByDefinition(AgelessGuardian)))
        .setBattlefield(Map(playerOne -> Seq(Plains, Plains).map(Strixhaven.cardPrintingsByDefinition)))

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
  }
}