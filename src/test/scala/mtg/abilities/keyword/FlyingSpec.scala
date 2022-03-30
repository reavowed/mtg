package mtg.abilities.keyword

import mtg.game.turns.TurnStep
import mtg.game.turns.turnBasedActions.DeclareBlockersChoice
import mtg.{SpecWithGameStateManager, TestCardCreation}

class FlyingSpec extends SpecWithGameStateManager with TestCardCreation {
  val CreatureWithFlying = zeroManaCreature(Flying, (1, 1))
  val CreatureWithReach = zeroManaCreature(Reach, (1, 1))
  val VanillaCreature = vanillaCreature(1, 1)

  "flying" should {
    "not offer blocks if defending player controls no creatures with flying or reach" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, CreatureWithFlying)
        .setBattlefield(playerTwo, VanillaCreature)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(CreatureWithFlying)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)

      manager.currentChoice should beSome(bePriorityChoice)
    }

    "only offer blocks for an attacker with flying to creatures with flying or reach" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, CreatureWithFlying)
        .setBattlefield(playerTwo, CreatureWithFlying, CreatureWithReach, VanillaCreature)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(CreatureWithFlying)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)

      manager.currentChoice must beSome(beAnInstanceOf[DeclareBlockersChoice])
      manager.currentChoice.get.asInstanceOf[DeclareBlockersChoice].possibleBlockers mustEqual Map(
        getPermanentId(CreatureWithFlying, playerTwo) -> Seq(getPermanentId(CreatureWithFlying, playerOne)),
        getPermanentId(CreatureWithReach, playerTwo) -> Seq(getPermanentId(CreatureWithFlying, playerOne)))
    }

    "allow a creature with flying to block another creature with flying" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, CreatureWithFlying)
        .setBattlefield(playerTwo, CreatureWithFlying)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith()
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(CreatureWithFlying, CreatureWithFlying)

      manager.currentChoice must beSome(bePriorityChoice)
    }

    "allow a creature with reach to block another creature with flying" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, CreatureWithFlying)
        .setBattlefield(playerTwo, CreatureWithReach)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith()
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(CreatureWithReach, CreatureWithFlying)

      manager.currentChoice must beSome(bePriorityChoice)
    }

    "not allow a creature without flying or reach to block a creature with flying" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, CreatureWithFlying)
        .setBattlefield(playerTwo, VanillaCreature)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith()
      manager.passUntilStep(TurnStep.DeclareBlockersStep)

      val stateBeforeBlock = manager.gameState
      manager.block(VanillaCreature, CreatureWithFlying)

      manager.gameState mustEqual stateBeforeBlock
    }
  }
}
