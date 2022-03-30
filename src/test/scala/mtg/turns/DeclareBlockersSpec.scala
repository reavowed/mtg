package mtg.turns

import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.SpellCard
import mtg.core.types.Type
import mtg.core.types.Type.Creature
import mtg.core.zones.Zone
import mtg.data.cards.kaldheim.GrizzledOutrider
import mtg.data.cards.strixhaven.{AgelessGuardian, SpinedKarok}
import mtg.data.cards.{Forest, Plains}
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.game.turns.TurnStep
import mtg.game.turns.turnBasedActions.{DeclareBlockersChoice, OrderBlockersChoice}
import mtg.instructions.nounPhrases.Target
import mtg.instructions.verbs.Destroy
import mtg.parts.costs.ManaCost
import mtg.{SpecWithGameStateManager, TestCardCreation}

class DeclareBlockersSpec extends SpecWithGameStateManager with TestCardCreation {
  val VanillaOneOne = vanillaCreature(1, 1)
  val VanillaTwoTwo = vanillaCreature(2, 2)

  "declare blockers step" should {
    // TODO: implement
//    "be skipped if no attackers" in {
//      val initialState = emptyGameObjectState
//        .setBattlefield(playerOne, VanillaOneOne)
//        .setBattlefield(playerTwo, VanillaOneOne)
//
//      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
//      manager.passUntilStep(TurnStep.DeclareAttackersStep)
//      manager.attackWith(playerOne, Nil: _*)
//      manager.passPriority(playerOne)
//      manager.passPriority(playerTwo)
//
//      manager.gameState.currentStep must beSome[TurnStep](TurnStep.EndOfCombatStep)
//    }

    "offer choice to block with an untapped creature" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, AgelessGuardian)
        .setBattlefield(playerOne, Plains, 2)
        .setHand(playerTwo, SpinedKarok)
        .setBattlefield(playerTwo, Forest, 3)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      // Cast attacker
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 2)
      manager.castSpell(playerOne, AgelessGuardian)

      // Cast blocker
      manager.passUntilTurnAndPhase(2, PrecombatMainPhase)
      manager.activateAbilities(playerTwo, Forest, 3)
      manager.castSpell(playerTwo, SpinedKarok)

      manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)
      manager.attackWith(AgelessGuardian)
      manager.passUntilTurnAndStep(3, TurnStep.DeclareBlockersStep)

      manager.currentChoice must beSome(beAnInstanceOf[DeclareBlockersChoice])
      manager.currentChoice.get.asInstanceOf[DeclareBlockersChoice].possibleBlockers mustEqual Map(getId(SpinedKarok) -> Seq(getId(AgelessGuardian)))
    }

    "require ordering if a single creature blocks an attacker" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, AgelessGuardian)
        .setBattlefield(playerOne, Plains, 2)
        .setHand(playerTwo, SpinedKarok)
        .setBattlefield(playerTwo, Forest, 3)
      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      // Cast attacker
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Plains, 2)
      manager.castSpell(playerOne, AgelessGuardian)

      // Cast blockers
      manager.passUntilTurnAndPhase(2, PrecombatMainPhase)
      manager.activateAbilities(playerTwo, Forest, 3)
      manager.castSpell(playerTwo, SpinedKarok)

      // Attack and block
      manager.passUntilTurnAndStep(3, TurnStep.DeclareAttackersStep)
      manager.attackWith(AgelessGuardian)
      manager.passUntilTurnAndStep(3, TurnStep.DeclareBlockersStep)
      manager.block(SpinedKarok, AgelessGuardian)

      manager.currentChoice must beSome(not(beAnInstanceOf[OrderBlockersChoice]))
    }

    "require ordering if multiple creatures block the same attacker" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setHand(playerOne, AgelessGuardian)
        .setBattlefield(playerOne, Plains, 2)
        .setHand(playerTwo, SpinedKarok, GrizzledOutrider)
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
      manager.attackWith(AgelessGuardian)
      manager.passUntilTurnAndStep(3, TurnStep.DeclareBlockersStep)
      manager.block((SpinedKarok, AgelessGuardian), (GrizzledOutrider, AgelessGuardian))

      manager.currentChoice must beSome(beAnInstanceOf[OrderBlockersChoice])
      manager.currentChoice.get.asInstanceOf[OrderBlockersChoice].playerToAct mustEqual playerOne
      manager.currentChoice.get.asInstanceOf[OrderBlockersChoice].attacker mustEqual manager.getCard(Zone.Battlefield, AgelessGuardian).objectId
      manager.currentChoice.get.asInstanceOf[OrderBlockersChoice].blockers must contain(exactly(
        manager.getCard(Zone.Battlefield, GrizzledOutrider).objectId,
        manager.getCard(Zone.Battlefield, SpinedKarok).objectId))
    }

    "not require blockers if the only attacker has been removed" in {
      val DestroySpell = new SpellCard(
        "Destroy Spell",
        ManaCost(0),
        Type.Instant,
        Destroy(Target(Creature)))

      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, VanillaOneOne)
        .setBattlefield(playerTwo, VanillaTwoTwo)
        .setHand(playerTwo, DestroySpell)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaOneOne)
      manager.passPriority(playerOne)
      manager.castSpell(playerTwo, DestroySpell)
      manager.chooseCard(playerTwo, VanillaOneOne)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)

      manager.currentChoice must beSome(bePriorityChoice)
    }
  }
}
