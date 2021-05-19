package mtg.game.actions

import mtg._
import mtg.data.cards.strixhaven.AgelessGuardian
import mtg.data.cards.{Forest, Plains}
import mtg.data.sets.Strixhaven
import mtg.game.objects.CardObject
import mtg.game.state.history.LogEvent
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.{PriorityChoice, StartNextTurnAction}

class PlayLandSpec extends SpecWithGameStateManager {
  "priority choice" should {
    "list playable lands from hand" in {
      val hand = Seq(Plains, Forest, AgelessGuardian).map(Strixhaven.getCard(_).get)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, hand)
      val plainsObject = playerOne.hand(initialState).mapFind(_.asOptionalInstanceOf[CardObject].filter(_.card.printing.cardDefinition == Plains)).get
      val forestObject = playerOne.hand(initialState).mapFind(_.asOptionalInstanceOf[CardObject].filter(_.card.printing.cardDefinition == Forest)).get

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      manager.currentGameState.pendingActions.head should bePriorityForPlayer(playerOne)
      manager.currentGameState.pendingActions.head.asInstanceOf[PriorityChoice].playableLands must contain(exactly(plainsObject, forestObject))
    }
  }

  "playing a land" should {
    "move the land to the battlefield" in {
      val hand = Seq(Plains, Forest, AgelessGuardian).map(Strixhaven.getCard(_).get)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, hand)
      val plainsObject = playerOne.hand(initialState).mapFind(_.asOptionalInstanceOf[CardObject].filter(_.card.printing.cardDefinition == Plains)).get

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.handleDecision("Play " + plainsObject.objectId.sequentialId, playerOne)

      manager.currentGameState.gameObjectState.battlefield must contain(exactly(beCardObject(plainsObject.card)))
      manager.currentGameState.gameObjectState.hands(playerOne) must not(contain(beCardObject(plainsObject.card)))
    }
    "log an event" in {
      val hand = Seq(Plains, Forest, AgelessGuardian).map(Strixhaven.getCard(_).get)
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, hand)
      val plainsObject = playerOne.hand(initialState).mapFind(_.asOptionalInstanceOf[CardObject].filter(_.card.printing.cardDefinition == Plains)).get

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.handleDecision("Play " + plainsObject.objectId.sequentialId, playerOne)

      manager.currentGameState.gameHistory.logEvents.last.logEvent mustEqual LogEvent.PlayedLand(playerOne, "Plains")
    }
  }
}
