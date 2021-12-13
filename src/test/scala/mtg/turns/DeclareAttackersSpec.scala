package mtg.turns

import mtg.SpecWithGameStateManager
import mtg.data.cards.Plains
import mtg.data.cards.m21.AlpineWatchdog
import mtg.data.cards.strixhaven.AgelessGuardian
import mtg.game.Zone
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.TurnStep
import mtg.game.turns.priority.PriorityChoice
import mtg.game.turns.turnBasedActions.DeclareAttackersChoice

class DeclareAttackersSpec extends SpecWithGameStateManager {
  "declare attackers" should {
    "not allow a creature that was cast this turn to attack" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, Seq(AgelessGuardian))
        .setBattlefield(playerOne, Seq(Plains, Plains))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      // Tap mana and cast creature
      manager.activateFirstAbility(playerOne, Plains)
      manager.activateFirstAbility(playerOne, Plains)
      manager.castSpell(playerOne, AgelessGuardian)

      manager.passUntilStep(TurnStep.DeclareAttackersStep)

      manager.currentChoice must beSome(beAnInstanceOf[PriorityChoice])
    }

    "allow a creature that was cast last turn to attack" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, Seq(AgelessGuardian))
        .setBattlefield(playerOne, Seq(Plains, Plains))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      // Tap mana and cast creature
      manager.activateFirstAbility(playerOne, Plains)
      manager.activateFirstAbility(playerOne, Plains)
      manager.castSpell(playerOne, AgelessGuardian)

      manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)

      manager.currentChoice must beSome(beAnInstanceOf[DeclareAttackersChoice])
      manager.currentChoice.get.asInstanceOf[DeclareAttackersChoice].possibleAttackers must contain(exactly(
        manager.getCard(Zone.Battlefield, AgelessGuardian).objectId
      ))
    }

    "tap a creature declared as an attacker" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setBattlefield(playerOne, AgelessGuardian)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, AgelessGuardian)

      manager.getCard(Zone.Battlefield, AgelessGuardian) must beTapped
    }

    "not tap a creature with vigilance declared as an attacker" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setBattlefield(playerOne, AlpineWatchdog)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, AlpineWatchdog)

      manager.getCard(Zone.Battlefield, AlpineWatchdog) must not(beTapped)
    }

    // TODO: Can't declare creature as attacker if gained control of it this turn
    // TODO: Can't declare creature as attacker if returned by control-changing effect ending this turn
  }

  // TODO: Declare blockers / damage skipped if no attackers declared or creatures put onto battlefield attacking
}
