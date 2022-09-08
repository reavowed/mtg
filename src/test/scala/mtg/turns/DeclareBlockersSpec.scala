package mtg.turns

import mtg.abilities.builder.TypeConversions._
import mtg.cards.patterns.SpellCard
import mtg.definitions.types.Type
import mtg.definitions.types.Type.Creature
import mtg.game.turns.TurnStep
import mtg.game.turns.turnBasedActions.{DeclareBlockersChoice, OrderBlockersChoice}
import mtg.instructions.nounPhrases.Target
import mtg.instructions.verbs.Destroy
import mtg.parts.costs.ManaCost
import mtg.{SpecWithGameStateManager, TestCardCreation}

class DeclareBlockersSpec extends SpecWithGameStateManager with TestCardCreation {
  val VanillaOneOne = vanillaCreature(1, 1)
  val VanillaTwoTwo = vanillaCreature(2, 2)
  val VanillaOneTwo = vanillaCreature(1, 2)
  val VanillaOneThree = vanillaCreature(1, 3)

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
        .setBattlefield(playerOne, VanillaTwoTwo)
        .setBattlefield(playerTwo, VanillaOneOne)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaTwoTwo)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)

      manager.currentChoice must beSome(beAnInstanceOf[DeclareBlockersChoice])
      manager.currentChoice.get.asInstanceOf[DeclareBlockersChoice].possibleBlockers mustEqual Map(getId(VanillaOneOne) -> Seq(getId(VanillaTwoTwo)))
    }

    "not require ordering if a single creature blocks an attacker" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, VanillaTwoTwo)
        .setBattlefield(playerTwo, VanillaOneOne)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaTwoTwo)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(VanillaOneOne, VanillaTwoTwo)

      manager.currentChoice must beSome(not(beAnInstanceOf[OrderBlockersChoice]))
    }

    "require ordering if multiple creatures block the same attacker" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, VanillaTwoTwo)
        .setBattlefield(playerTwo, VanillaOneTwo, VanillaOneThree)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaTwoTwo)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block((VanillaOneTwo, VanillaTwoTwo), (VanillaOneThree, VanillaTwoTwo))

      manager.currentChoice must beSome(beAnInstanceOf[OrderBlockersChoice])
      manager.currentChoice.get.asInstanceOf[OrderBlockersChoice].playerToAct mustEqual playerOne
      manager.currentChoice.get.asInstanceOf[OrderBlockersChoice].attacker mustEqual manager.getCard(VanillaTwoTwo).objectId
      manager.currentChoice.get.asInstanceOf[OrderBlockersChoice].blockers must contain(exactly(
        manager.getCard(VanillaOneTwo).objectId,
        manager.getCard(VanillaOneThree).objectId))
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
