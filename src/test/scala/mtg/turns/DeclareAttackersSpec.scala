package mtg.turns

import mtg.SpecWithGameStateManager
import mtg.data.cards.Plains
import mtg.data.cards.strixhaven.AgelessGuardian
import mtg.game.Zone
import mtg.game.turns.{StartNextTurnAction, TurnStep}
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.priority.PriorityChoice
import mtg.game.turns.turnBasedActions.DeclareAttackersChoice

class DeclareAttackersSpec extends SpecWithGameStateManager {
  "declare attackers" should {
    "not allow a creature that was cast this turn to attack" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, Seq(AgelessGuardian))
        .setBattlefield(playerOne, Seq(Plains, Plains))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      // Tap mana and cast creature
      manager.activateFirstAbility(playerOne, Plains)
      manager.activateFirstAbility(playerOne, Plains)
      manager.castSpell(playerOne, AgelessGuardian)

      manager.passUntilStep(TurnStep.DeclareAttackersStep)

      manager.currentAction must beAnInstanceOf[PriorityChoice]
    }

    "allow a creature that was cast last turn to attack" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, Seq(AgelessGuardian))
        .setBattlefield(playerOne, Seq(Plains, Plains))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      // Tap mana and cast creature
      manager.activateFirstAbility(playerOne, Plains)
      manager.activateFirstAbility(playerOne, Plains)
      manager.castSpell(playerOne, AgelessGuardian)

      manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)

      manager.currentAction must beAnInstanceOf[DeclareAttackersChoice]
    }

    "tap a creature declared as an attacker" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, Seq(AgelessGuardian))
        .setBattlefield(playerOne, Seq(Plains, Plains))

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)

      // Tap mana and cast creature
      manager.activateFirstAbility(playerOne, Plains)
      manager.activateFirstAbility(playerOne, Plains)
      manager.castSpell(playerOne, AgelessGuardian)

      manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, AgelessGuardian)

      manager.getCard(Zone.Battlefield, AgelessGuardian) must beTapped
    }

    // TODO: Can't declare creature as attacker if gained control of it this turn
    // TODO: Can't declare creature as attacker if returned by control-changing effect ending this turn
  }

  // TODO: Declare blockers / damage skipped if no attackers declared or creatures put onto battlefield attacking
}
