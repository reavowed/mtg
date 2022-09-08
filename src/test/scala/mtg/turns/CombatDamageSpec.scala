package mtg.turns

import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.SpellCard
import mtg.definitions.types.Type
import mtg.definitions.types.Type.Creature
import mtg.definitions.zones.Zone
import mtg.game.turns.TurnStep
import mtg.game.turns.turnBasedActions.AssignCombatDamageChoice
import mtg.instructions.nounPhrases.Target
import mtg.instructions.verbs.Destroy
import mtg.parts.costs.ManaCost
import mtg.{SpecWithGameStateManager, TestCardCreation}

class CombatDamageSpec extends SpecWithGameStateManager with TestCardCreation {
  val VanillaOneThree = vanillaCreature(1, 3)
  val VanillaTwoFour = vanillaCreature(2, 4)
  val VanillaZeroThree = vanillaCreature(0, 3)
  val VanillaThreeThree = vanillaCreature(3, 3)
  val VanillaFiveFive = vanillaCreature(5, 5)

  // TODO: be skipped if no attackers

  "an unblocked creature should deal combat damage to defending player" in {
    val initialState = gameObjectStateWithInitialLibrariesAndHands
      .setBattlefield(playerOne, VanillaOneThree)

    val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
    manager.passUntilStep(TurnStep.DeclareAttackersStep)
    manager.attackWith(VanillaOneThree)
    manager.passUntilStep(TurnStep.CombatDamageStep)

    manager.gameState.gameObjectState.lifeTotals(playerTwo) mustEqual 19
  }

  "a blocked attacker" should {
    "deal damage to and be dealt damage by its blocker" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setBattlefield(playerOne, VanillaOneThree)
        .setBattlefield(playerTwo, VanillaTwoFour)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaOneThree)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(VanillaTwoFour, VanillaOneThree)
      manager.passUntilStep(TurnStep.CombatDamageStep)

      manager.gameState.gameObjectState.lifeTotals(playerOne) mustEqual 20
      manager.gameState.gameObjectState.lifeTotals(playerTwo) mustEqual 20
      manager.getPermanent(VanillaOneThree).markedDamage mustEqual 2
      manager.getPermanent(VanillaTwoFour).markedDamage mustEqual 1
    }

    "not deal excess damage to player if it doesn't have trample" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setBattlefield(playerOne, VanillaFiveFive)
        .setBattlefield(playerTwo, VanillaTwoFour)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaFiveFive)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(VanillaTwoFour, VanillaFiveFive)
      manager.passUntilStep(TurnStep.CombatDamageStep)

      manager.gameState.gameObjectState.lifeTotals(playerOne) mustEqual 20
      manager.gameState.gameObjectState.lifeTotals(playerTwo) mustEqual 20
    }

    "not deal damage if its blocker is removed from combat" in {
      val DestroySpell = new SpellCard(
        "Destroy Spell",
        ManaCost(0),
        Type.Instant,
        Destroy(Target(Creature)))
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setBattlefield(playerOne, VanillaOneThree)
        .setHand(playerOne, DestroySpell)
        .setBattlefield(playerTwo, VanillaTwoFour)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaOneThree)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(VanillaTwoFour, VanillaOneThree)
      manager.castSpell(playerOne, DestroySpell)
      manager.chooseCard(playerOne, VanillaTwoFour)
      manager.passUntilStep(TurnStep.CombatDamageStep)

      manager.gameState.gameObjectState.lifeTotals(playerOne) mustEqual 20
      manager.gameState.gameObjectState.lifeTotals(playerTwo) mustEqual 20
      manager.getPermanent(VanillaOneThree).markedDamage mustEqual 0
    }
  }

  "an attacking creature blocked by two creatures" should {
    "not have to assign damage if it cannot deal excess damage to the first blocking creature" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setBattlefield(playerOne, VanillaOneThree)
        .setBattlefield(playerTwo, VanillaTwoFour, VanillaZeroThree)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      // Attack and block
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaOneThree)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block((VanillaTwoFour, VanillaOneThree), (VanillaZeroThree, VanillaOneThree))
      manager.orderBlocks(playerOne, VanillaTwoFour, VanillaZeroThree)

      // Go to damage
      manager.passUntilStep(TurnStep.CombatDamageStep)

      // Assert
      manager.currentChoice must beSome(bePriorityChoice)
      manager.getPermanent(VanillaTwoFour).markedDamage mustEqual 1
      manager.getPermanent(VanillaZeroThree).markedDamage mustEqual 0
    }

    "have to assign damage if it can deal excess damage to the first blocking creature" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setBattlefield(playerOne, VanillaFiveFive)
        .setBattlefield(playerTwo, VanillaTwoFour, VanillaZeroThree)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      // Attack and block
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaFiveFive)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block((VanillaTwoFour, VanillaFiveFive), (VanillaZeroThree, VanillaFiveFive))
      manager.orderBlocks(playerOne, VanillaTwoFour, VanillaZeroThree)

      // Go to damage
      manager.passUntilStep(TurnStep.CombatDamageStep)

      // Assert
      manager.currentChoice must beSome(beAnInstanceOf[AssignCombatDamageChoice])
    }

    "not be allowed to assign less than lethal damage to the first blocker" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setBattlefield(playerOne, VanillaFiveFive)
        .setBattlefield(playerTwo, VanillaTwoFour, VanillaZeroThree)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      // Attack and block
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaFiveFive)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block((VanillaTwoFour, VanillaFiveFive), (VanillaZeroThree, VanillaFiveFive))
      manager.orderBlocks(playerOne, VanillaTwoFour, VanillaZeroThree)

      // Go to damage
      manager.passUntilStep(TurnStep.CombatDamageStep)
      manager.assignDamage(playerOne, (VanillaTwoFour, 2), (VanillaZeroThree, 3))

      // Assert
      manager.currentChoice must beSome(beAnInstanceOf[AssignCombatDamageChoice])
    }

    "be allowed to assign lethal damage to the first blocker and excess damage to the second" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setBattlefield(playerOne, VanillaFiveFive)
        .setBattlefield(playerTwo, VanillaTwoFour, VanillaZeroThree)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      // Attack and block
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaFiveFive)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block((VanillaTwoFour, VanillaFiveFive), (VanillaZeroThree, VanillaFiveFive))
      manager.orderBlocks(playerOne, VanillaTwoFour, VanillaZeroThree)

      // Go to damage
      manager.passUntilStep(TurnStep.CombatDamageStep)
      manager.assignDamage(playerOne, (VanillaTwoFour, 4), (VanillaZeroThree, 1))

      // Assert
      manager.currentChoice must beSome(bePriorityChoice)
      Zone.Graveyard(playerTwo)(manager.gameState) must contain(beCardObject(VanillaTwoFour))
      manager.getPermanent(VanillaZeroThree).markedDamage mustEqual 1
    }

    "be allowed to assign more than lethal damage to the first blocker" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setBattlefield(playerOne, VanillaFiveFive)
        .setBattlefield(playerTwo, VanillaTwoFour, VanillaZeroThree)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      // Attack and block
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaFiveFive)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block((VanillaTwoFour, VanillaFiveFive), (VanillaZeroThree, VanillaFiveFive))
      manager.orderBlocks(playerOne, VanillaTwoFour, VanillaZeroThree)

      // Go to damage
      manager.passUntilStep(TurnStep.CombatDamageStep)
      manager.assignDamage(playerOne, (VanillaTwoFour, 5), (VanillaZeroThree, 0))

      // Assert
      manager.currentChoice must beSome(bePriorityChoice)
      Zone.Graveyard(playerTwo)(manager.gameState) must contain(beCardObject(VanillaTwoFour))
      manager.getPermanent(VanillaZeroThree).markedDamage mustEqual 0
    }

    "deal full damage to the remaining blocker if one blocker is removed" in {
      val DestroySpell = new SpellCard(
        "Destroy Spell",
        ManaCost(0),
        Type.Instant,
        Destroy(Target(Creature)))
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setBattlefield(playerOne, VanillaThreeThree)
        .setHand(playerOne, DestroySpell)
        .setBattlefield(playerTwo, VanillaOneThree, VanillaTwoFour)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaThreeThree)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block((VanillaOneThree, VanillaThreeThree), (VanillaTwoFour, VanillaThreeThree))
      manager.orderBlocks(playerOne, VanillaOneThree, VanillaTwoFour)
      manager.castSpell(playerOne, DestroySpell)
      manager.chooseCard(playerOne, VanillaOneThree)
      manager.passUntilStep(TurnStep.CombatDamageStep)

      manager.gameState.gameObjectState.lifeTotals(playerOne) mustEqual 20
      manager.gameState.gameObjectState.lifeTotals(playerTwo) mustEqual 20
      manager.getPermanent(VanillaTwoFour).markedDamage mustEqual 3

    }
  }
}
