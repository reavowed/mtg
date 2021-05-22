package mtg.game.actions

import mtg._
import mtg.data.cards.strixhaven.AgelessGuardian
import mtg.data.cards.{Forest, Plains, Swamp}
import mtg.data.sets.Strixhaven
import mtg.game.objects.{CardObject, GameObject}
import mtg.game.state.history.LogEvent
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.priority.PriorityChoice
import mtg.game.turns.{StartNextTurnAction, TurnStep}
import org.specs2.matcher.Matcher

class PlayLandSpec extends SpecWithGameStateManager {
  def bePlayLandAction(land: GameObject): Matcher[PlayLandAction] = {
    {(playLandAction: PlayLandAction) => playLandAction.land} ^^ beObjectWithState(land)
  }

  "land actions with priority" should {
    "be available for all lands in hand in main phase of own turn with empty stack" in {
      val hand = Seq(Plains, Forest, AgelessGuardian).map(Strixhaven.cardPrintingsByDefinition)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, hand)

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      val plainsObject = playerOne.hand(initialState).getCard(Plains)
      val forestObject = playerOne.hand(initialState).getCard(Forest)
      manager.currentGameState.pendingActions.head should bePriorityForPlayer(playerOne)
      manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[PlayLandAction] must contain(exactly(
        bePlayLandAction(plainsObject),
        bePlayLandAction(forestObject)))
    }

    "not be available on another player's turn" in {
      val hand = Seq(Plains, Forest, AgelessGuardian).map(Strixhaven.cardPrintingsByDefinition)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerTwo, hand)

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.passPriority(playerOne)

      manager.currentGameState.pendingActions.head should bePriorityForPlayer(playerTwo)
      manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[PlayLandAction] must beEmpty
    }

    "not be available if player has already played a land this turn" in {
      val hand = Seq(Plains, Forest, AgelessGuardian).map(Strixhaven.cardPrintingsByDefinition)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, hand)

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.playLand(playerOne.hand(initialState).getCard(Plains), playerOne)

      manager.currentGameState.pendingActions.head should bePriorityForPlayer(playerOne)
      manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[PlayLandAction] must beEmpty
    }

    "be available for all lands in hand if player has not played a land this turn" in {
      val hand = Seq(Plains, Forest, AgelessGuardian).map(Strixhaven.cardPrintingsByDefinition)
      val library = Seq(Swamp).map(Strixhaven.cardPrintingsByDefinition)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, hand).setLibrary(playerOne, library)

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.playLand(playerOne.hand(manager.currentGameState).getCard(Plains), playerOne)
      manager.passUntilTurn(3)
      manager.passUntilPhase(PrecombatMainPhase)

      manager.currentGameState.pendingActions.head should bePriorityForPlayer(playerOne)
      manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[PlayLandAction] must contain(exactly(
        bePlayLandAction(playerOne.hand(manager.currentGameState).getCard(Forest)),
        bePlayLandAction(playerOne.hand(manager.currentGameState).getCard(Swamp))))
    }

    "not be available in upkeep" in {
      val hand = Seq(Plains, Forest, AgelessGuardian).map(Strixhaven.cardPrintingsByDefinition)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, hand)

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.currentGameState.currentStep must beSome[TurnStep](TurnStep.UpkeepStep)
      manager.currentGameState.pendingActions.head should bePriorityForPlayer(playerOne)
      manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].availableActions.ofType[PlayLandAction] must beEmpty
    }

    // TODO: no land actions if stack is non-empty
    // TODO: land actions for lands in other zones with Crucible of Worlds effects
    // TODO: no land actions with Agressive Mining effects
    // TODO: no land actions from hand with Fires of Invention effects
    // TODO: land actions after play with Explore / Azusa
  }

  "playing a land as a special action" should {
    "move the land to the battlefield" in {
      val hand = Seq(Plains, Forest, AgelessGuardian).map(Strixhaven.cardPrintingsByDefinition)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, hand)
      val plainsObject = playerOne.hand(initialState).mapFind(_.asOptionalInstanceOf[CardObject].filter(_.card.printing.cardDefinition == Plains)).get

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.playLand(plainsObject, playerOne)

      manager.currentGameState.gameObjectState.battlefield must contain(exactly(beCardObject(plainsObject.card)))
      manager.currentGameState.gameObjectState.hands(playerOne) must not(contain(beCardObject(plainsObject.card)))
    }
    "log an event" in {
      val hand = Seq(Plains, Forest, AgelessGuardian).map(Strixhaven.cardPrintingsByDefinition)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, hand)
      val plainsObject = playerOne.hand(initialState).mapFind(_.asOptionalInstanceOf[CardObject].filter(_.card.printing.cardDefinition == Plains)).get

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.handleDecision("Play " + plainsObject.objectId.sequentialId, playerOne)

      manager.currentGameState.gameHistory.logEvents.last.logEvent mustEqual LogEvent.PlayedLand(playerOne, "Plains")
    }
  }

  // TODO: test playing a land as instruction from effect
  // - not on other player's turn
  // - not if effect forbids
}
