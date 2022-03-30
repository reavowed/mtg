package mtg.turns

import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.SpellCard
import mtg.core.types.Type
import mtg.core.types.Type.Creature
import mtg.{SpecWithGameStateManager, TestCards}
import mtg.core.zones.Zone
import mtg.data.cards.kaldheim.GrizzledOutrider
import mtg.data.cards.strixhaven.{AgelessGuardian, SpinedKarok}
import mtg.data.cards.{Forest, Plains}
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.TurnStep
import mtg.game.turns.turnBasedActions.AssignCombatDamageChoice
import mtg.instructions.nounPhrases.Target
import mtg.instructions.verbs.Destroy
import mtg.parts.costs.ManaCost

class CombatDamageSpec extends SpecWithGameStateManager {
  val VanillaOneThree = TestCards.vanillaCreature(1, 3)
  val VanillaTwoFour = TestCards.vanillaCreature(2, 4)
  val VanillaThreeThree = TestCards.vanillaCreature(3, 3)
  val VanillaFiveFive = TestCards.vanillaCreature(5, 5)

  // TODO: be skipped if no attackers

  "an unblocked creature should deal combat damage to defending player" in {
    val initialState = gameObjectStateWithInitialLibrariesAndHands
      .setBattlefield(playerOne, VanillaOneThree)

    val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
    manager.passUntilStep(TurnStep.DeclareAttackersStep)
    manager.attackWith(playerOne, VanillaOneThree)
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
      manager.attackWith(playerOne, VanillaOneThree)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, VanillaTwoFour, VanillaOneThree)
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
      manager.attackWith(playerOne, VanillaFiveFive)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, VanillaTwoFour, VanillaFiveFive)
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
      manager.attackWith(playerOne, VanillaOneThree)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, VanillaTwoFour, VanillaOneThree)
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
        .setHand(playerOne, AgelessGuardian)
        .setBattlefield(playerOne, Plains, 2)
        .setHand(playerTwo, Seq(SpinedKarok, GrizzledOutrider))
        .setBattlefield(playerTwo, Forest, 8)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      // Cast attacker
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 2)
      manager.castSpell(playerOne, AgelessGuardian)

      // Cast blockers
      manager.passUntilTurnAndPhase(2, PrecombatMainPhase)
      manager.activateAbilities(playerTwo, Forest, 3)
      manager.castSpell(playerTwo, SpinedKarok)
      manager.passUntilStackEmpty()
      manager.activateAbilities(playerTwo, Forest, 5)
      manager.castSpell(playerTwo, GrizzledOutrider)

      // Attack and block
      manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, AgelessGuardian)
      manager.passUntilTurnAndStep(3, TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, (SpinedKarok, AgelessGuardian), (GrizzledOutrider, AgelessGuardian))
      manager.orderBlocks(playerOne, SpinedKarok, GrizzledOutrider)

      // Go to damage
      manager.passUntilStep(TurnStep.CombatDamageStep)

      // Assert
      manager.currentChoice must beSome(bePriorityChoice)
      manager.getPermanent(SpinedKarok).markedDamage mustEqual 1
      manager.getPermanent(GrizzledOutrider).markedDamage mustEqual 0
    }

    "have to assign damage if it can deal excess damage to the first blocking creature" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, GrizzledOutrider)
        .setBattlefield(playerOne, Forest, 5)
        .setHand(playerTwo, Seq(AgelessGuardian, SpinedKarok))
        .setBattlefield(playerTwo, Seq.fill(2)(Plains) ++ Seq.fill(3)(Forest))
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      // Cast attacker
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 5)
      manager.castSpell(playerOne, GrizzledOutrider)

      // Cast blockers
      manager.passUntilTurnAndPhase(2, PrecombatMainPhase)
      manager.activateAbilities(playerTwo, Forest, 3)
      manager.castSpell(playerTwo, SpinedKarok)
      manager.passUntilStackEmpty()
      manager.activateAbilities(playerTwo, Plains, 2)
      manager.castSpell(playerTwo, AgelessGuardian)

      // Attack and block
      manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, GrizzledOutrider)
      manager.passUntilTurnAndStep(3, TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, (SpinedKarok, GrizzledOutrider), (AgelessGuardian, GrizzledOutrider))
      manager.orderBlocks(playerOne, AgelessGuardian, SpinedKarok)

      // Go to damage
      manager.passUntilStep(TurnStep.CombatDamageStep)

      // Assert
      manager.currentChoice must beSome(beAnInstanceOf[AssignCombatDamageChoice])
    }

    "not be allowed to assign less than lethal damage to the first blocker" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, GrizzledOutrider)
        .setBattlefield(playerOne, Forest, 5)
        .setHand(playerTwo, Seq(AgelessGuardian, SpinedKarok))
        .setBattlefield(playerTwo, Seq.fill(2)(Plains) ++ Seq.fill(3)(Forest))
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      // Cast attacker
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 5)
      manager.castSpell(playerOne, GrizzledOutrider)

      // Cast blockers
      manager.passUntilTurnAndPhase(2, PrecombatMainPhase)
      manager.activateAbilities(playerTwo, Forest, 3)
      manager.castSpell(playerTwo, SpinedKarok)
      manager.passUntilStackEmpty()
      manager.activateAbilities(playerTwo, Plains, 2)
      manager.castSpell(playerTwo, AgelessGuardian)

      // Attack and block
      manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, GrizzledOutrider)
      manager.passUntilTurnAndStep(3, TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, (SpinedKarok, GrizzledOutrider), (AgelessGuardian, GrizzledOutrider))
      manager.orderBlocks(playerOne, AgelessGuardian, SpinedKarok)

      // Go to damage
      manager.passUntilStep(TurnStep.CombatDamageStep)
      manager.assignDamage(playerOne, (AgelessGuardian, 1), (SpinedKarok, 4))

      // Assert
      manager.currentChoice must beSome(beAnInstanceOf[AssignCombatDamageChoice])
    }

    "be allowed to assign lethal damage to the first blocker and excess damage to the second" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, GrizzledOutrider)
        .setBattlefield(playerOne, Forest, 5)
        .setHand(playerTwo, Seq(AgelessGuardian, SpinedKarok))
        .setBattlefield(playerTwo, Seq.fill(2)(Plains) ++ Seq.fill(3)(Forest))
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      // Cast attacker
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 5)
      manager.castSpell(playerOne, GrizzledOutrider)

      // Cast blockers
      manager.passUntilTurnAndPhase(2, PrecombatMainPhase)
      manager.activateAbilities(playerTwo, Forest, 3)
      manager.castSpell(playerTwo, SpinedKarok)
      manager.passUntilStackEmpty()
      manager.activateAbilities(playerTwo, Plains, 2)
      manager.castSpell(playerTwo, AgelessGuardian)

      // Attack and block
      manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, GrizzledOutrider)
      manager.passUntilTurnAndStep(3, TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, (SpinedKarok, GrizzledOutrider), (AgelessGuardian, GrizzledOutrider))
      manager.orderBlocks(playerOne, AgelessGuardian, SpinedKarok)

      // Go to damage
      manager.passUntilStep(TurnStep.CombatDamageStep)
      manager.assignDamage(playerOne, (AgelessGuardian, 4), (SpinedKarok, 1))

      // Assert
      manager.currentChoice must beSome(bePriorityChoice)
      Zone.Graveyard(playerTwo)(manager.gameState) must contain(beCardObject(AgelessGuardian))
      manager.getPermanent(SpinedKarok).markedDamage mustEqual 1
    }

    "be allowed to assign more than lethal damage to the first blocker" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, GrizzledOutrider)
        .setBattlefield(playerOne, Forest, 5)
        .setHand(playerTwo, Seq(AgelessGuardian, SpinedKarok))
        .setBattlefield(playerTwo, Seq.fill(2)(Plains) ++ Seq.fill(3)(Forest))
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      // Cast attacker
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Forest, 5)
      manager.castSpell(playerOne, GrizzledOutrider)

      // Cast blockers
      manager.passUntilTurnAndPhase(2, PrecombatMainPhase)
      manager.activateAbilities(playerTwo, Forest, 3)
      manager.castSpell(playerTwo, SpinedKarok)
      manager.passUntilStackEmpty()
      manager.activateAbilities(playerTwo, Plains, 2)
      manager.castSpell(playerTwo, AgelessGuardian)

      // Attack and block
      manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, GrizzledOutrider)
      manager.passUntilTurnAndStep(3, TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, (SpinedKarok, GrizzledOutrider), (AgelessGuardian, GrizzledOutrider))
      manager.orderBlocks(playerOne, AgelessGuardian, SpinedKarok)

      // Go to damage
      manager.passUntilStep(TurnStep.CombatDamageStep)
      manager.assignDamage(playerOne, (AgelessGuardian, 5), (SpinedKarok, 0))

      // Assert
      manager.currentChoice must beSome(bePriorityChoice)
      Zone.Graveyard(playerTwo)(manager.gameState) must contain(beCardObject(AgelessGuardian))
      manager.getPermanent(SpinedKarok).markedDamage mustEqual 0
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
        .setBattlefield(playerTwo, Seq(VanillaOneThree, VanillaTwoFour))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, VanillaThreeThree)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, (VanillaOneThree, VanillaThreeThree), (VanillaTwoFour, VanillaThreeThree))
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
