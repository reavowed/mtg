package mtg.game.actions

import mtg._
import mtg.cards.CardDefinition
import mtg.data.cards.strixhaven.AgelessGuardian
import mtg.data.cards.{Forest, Plains, Swamp}
import mtg.data.sets.Strixhaven
import mtg.game.actions.cast.CastSpellAction
import mtg.game.objects.{CardObject, GameObject}
import mtg.game.state.history.LogEvent
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.priority.PriorityChoice
import mtg.game.turns.{StartNextTurnAction, TurnStep}
import org.specs2.matcher.Matcher

class PlayLandSpec extends SpecWithGameStateManager {
  def bePlayLandAction(land: CardDefinition): Matcher[PlayLandAction] = {
    {(playLandAction: PlayLandAction) => playLandAction.land.gameObject} ^^ beCardObject(land)
  }

  "land actions with priority" should {
    "be available for all lands in hand in main phase of own turn with empty stack" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, Seq(Plains, Forest, AgelessGuardian))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      manager.currentAction must bePriorityChoice.forPlayer(playerOne).withAvailableLands(contain(exactly(
        bePlayLandAction(Plains),
        bePlayLandAction(Forest))))
    }

    "not be available on another player's turn" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerTwo, Seq(Plains, Forest, AgelessGuardian))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.passPriority(playerOne)

      manager.currentAction must bePriorityChoice.forPlayer(playerTwo).withAvailableLands(beEmpty)
    }

    "not be available if player has already played a land this turn" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, Seq(Plains, Forest, AgelessGuardian))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.playLand(playerOne, Plains)

      manager.currentAction must bePriorityChoice.forPlayer(playerOne).withAvailableLands(beEmpty)
    }

    "be available for all lands in hand if player has not played a land this turn" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, Seq(Plains, Forest, AgelessGuardian))
        .setLibrary(playerOne, Seq(Swamp))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.playLand(playerOne, Plains)
      manager.passUntilTurn(3)
      manager.passUntilPhase(PrecombatMainPhase)

      manager.currentAction must bePriorityChoice.forPlayer(playerOne)
        .withAvailableLands(contain(exactly(bePlayLandAction(Forest), bePlayLandAction(Swamp))))
    }

    "not be available in upkeep" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, Seq(Plains, Forest, AgelessGuardian))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))

      manager.currentAction must bePriorityChoice.forPlayer(playerOne).withAvailableLands(beEmpty)
    }

    "not be available if stack is non-empty" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, Seq(AgelessGuardian, Plains))
        .setBattlefield(playerOne, Seq(Plains, Plains))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      // Tap mana and cast spell
      manager.activateFirstAbility(playerOne, Plains)
      manager.activateFirstAbility(playerOne, Plains)
      manager.castSpell(playerOne, AgelessGuardian)

      manager.currentAction must bePriorityChoice.forPlayer(playerOne).withAvailableLands(beEmpty)
    }

    // TODO: no land actions if stack is non-empty
    // TODO: land actions for lands in other zones with Crucible of Worlds effects
    // TODO: no land actions with Agressive Mining effects
    // TODO: no land actions from hand with Fires of Invention effects
    // TODO: land actions after play with Explore / Azusa
  }

  "playing a land as a special action" should {
    "move the land to the battlefield" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, Seq(Plains, Forest, AgelessGuardian))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.playLand(playerOne, Plains)

      manager.currentGameState.gameObjectState.battlefield must contain(exactly(beCardObject(Plains)))
      manager.currentGameState.gameObjectState.hands(playerOne) must not(contain(beCardObject(Plains)))
    }
    "log an event" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, Seq(Plains, Forest, AgelessGuardian))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.playLand(playerOne, Plains)

      manager.currentGameState.gameHistory.logEvents.last.logEvent mustEqual LogEvent.PlayedLand(playerOne, "Plains")
    }
  }

  // TODO: test playing a land as instruction from effect
  // - not on other player's turn
  // - not if effect forbids
}
