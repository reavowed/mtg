package mtg.game.actions

import mtg.SpecWithGameStateManager
import mtg.data.cards.strixhaven.AgelessGuardian
import mtg.data.cards.{Forest, Plains}
import mtg.data.sets.Strixhaven
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
}
