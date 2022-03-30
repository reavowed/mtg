package mtg.turns

import mtg.abilities.keyword.Vigilance
import mtg.{SpecWithGameStateManager, TestCardCreation}
import mtg.core.zones.Zone
import mtg.data.cards.Plains
import mtg.game.priority.PriorityChoice
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.TurnStep
import mtg.game.turns.turnBasedActions.DeclareAttackersChoice

class DeclareAttackersSpec extends SpecWithGameStateManager with TestCardCreation {
  val VanillaCreature = vanillaCreature(1, 3)
  val CreatureWithVigilance = zeroManaCreature(Vigilance, (1,1))

  "declare attackers" should {
    "not allow a creature that was cast this turn to attack" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, VanillaCreature)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.castSpell(playerOne, VanillaCreature)

      manager.passUntilStep(TurnStep.DeclareAttackersStep)

      manager.currentChoice must beSome(beAnInstanceOf[PriorityChoice])
    }

    "allow a creature that was cast last turn to attack" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, VanillaCreature)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.castSpell(playerOne, VanillaCreature)

      manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)

      manager.currentChoice must beSome(beAnInstanceOf[DeclareAttackersChoice])
      manager.currentChoice.get.asInstanceOf[DeclareAttackersChoice].possibleAttackers must contain(exactly(
        manager.getCard(VanillaCreature).objectId
      ))
    }

    "tap a creature declared as an attacker" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setBattlefield(playerOne, VanillaCreature)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaCreature)

      manager.getCard(Zone.Battlefield, VanillaCreature) must beTapped
    }

    "not tap a creature with vigilance declared as an attacker" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setBattlefield(playerOne, CreatureWithVigilance)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(CreatureWithVigilance)

      manager.getCard(Zone.Battlefield, CreatureWithVigilance) must not(beTapped)
    }

    // TODO: Can't declare creature as attacker if gained control of it this turn
    // TODO: Can't declare creature as attacker if returned by control-changing effect ending this turn
  }

  // TODO: Declare blockers / damage skipped if no attackers declared or creatures put onto battlefield attacking
}
