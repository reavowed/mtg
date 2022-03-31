package mtg.game.actions

import mtg._
import mtg.cards.CardDefinition
import mtg.game.priority.actions.PlayLandAction
import mtg.game.state.history.LogEvent
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.sets.alpha.cards.{Forest, Plains}
import org.specs2.matcher.Matcher

class PlayLandSpec extends SpecWithGameStateManager with TestCardCreation {
  val VanillaCreature = vanillaCreature(1, 1)
  def bePlayLandAction(land: CardDefinition): Matcher[PlayLandAction] = {
    {(playLandAction: PlayLandAction) => playLandAction.land.gameObject} ^^ beCardObject(land)
  }

  "land actions with priority" should {
    "be available for all lands in hand in main phase of own turn with empty stack" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, Plains, Forest)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne).withAvailableLands(contain(exactly(
        bePlayLandAction(Plains),
        bePlayLandAction(Forest)))))
    }

    "not be available on another player's turn" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerTwo, Plains)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.passPriority(playerOne)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerTwo).withAvailableLands(beEmpty))
    }

    "not be available if player has already played a land this turn" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, Plains, Forest)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.playLand(playerOne, Plains)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne).withAvailableLands(beEmpty))
    }

    "be available for all lands in hand if player has not played a land this turn" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, Plains)
        .setLibrary(playerOne, Forest)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.playLand(playerOne, Plains)
      manager.passUntilTurn(3)
      manager.passUntilPhase(PrecombatMainPhase)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne)
        .withAvailableLands(contain(exactly(bePlayLandAction(Forest)))))
    }

    "not be available in upkeep" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, Plains)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne).withAvailableLands(beEmpty))
    }

    "not be available if stack is non-empty" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, VanillaCreature, Plains)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      // Tap mana and cast spell
      manager.castSpell(playerOne, VanillaCreature)

      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne).withAvailableLands(beEmpty))
    }

    // TODO: no land actions if stack is non-empty
    // TODO: land actions for lands in other zones with Crucible of Worlds effects
    // TODO: no land actions with Agressive Mining effects
    // TODO: no land actions from hand with Fires of Invention effects
    // TODO: land actions after play with Explore / Azusa
  }

  "playing a land as a special action" should {
    "move the land to the battlefield" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, Plains)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.playLand(playerOne, Plains)

      manager.gameState.gameObjectState.battlefield must contain(exactly(beCardObject(Plains)))
      manager.gameState.gameObjectState.hands(playerOne) must not(contain(beCardObject(Plains)))
    }
    "log an event" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, Plains, Forest, VanillaCreature)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.playLand(playerOne, Plains)

      manager.gameState.gameHistory.logEvents.last.logEvent mustEqual LogEvent.PlayedLand(playerOne, "Plains")
    }
  }

  // TODO: test playing a land as instruction from effect
  // - not on other player's turn
  // - not if effect forbids
}
