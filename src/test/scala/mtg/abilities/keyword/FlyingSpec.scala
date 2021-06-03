package mtg.abilities.keyword

import mtg.SpecWithGameStateManager
import mtg.data.cards.alpha.AirElemental
import mtg.data.cards.kaldheim.GnottvoldRecluse
import mtg.data.cards.m21.ConcordiaPegasus
import mtg.data.cards.strixhaven.{AgelessGuardian, SpinedKarok}
import mtg.game.turns.turnBasedActions.DeclareBlockersChoice
import mtg.game.turns.{StartNextTurnAction, TurnStep}

class FlyingSpec extends SpecWithGameStateManager {
  "flying" should {
    "not offer blocks if defending player controls no creatures with flying or reach" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, ConcordiaPegasus)
        .setBattlefield(playerTwo, AgelessGuardian)

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, ConcordiaPegasus)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)

      manager.currentAction must bePriorityChoice
    }

    "only offer blocks for an attacker with flying to creatures with flying or reach" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Seq(ConcordiaPegasus, SpinedKarok))
        .setBattlefield(playerTwo, Seq(AirElemental, AgelessGuardian, GnottvoldRecluse))

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, ConcordiaPegasus, SpinedKarok)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)

      manager.currentAction must beAnInstanceOf[DeclareBlockersChoice]
      manager.currentAction.asInstanceOf[DeclareBlockersChoice].possibleBlockers mustEqual Map(
        getId(AgelessGuardian) -> Seq(getId(SpinedKarok)),
        getId(AirElemental) -> Seq(getId(ConcordiaPegasus), getId(SpinedKarok)),
        getId(GnottvoldRecluse) -> Seq(getId(ConcordiaPegasus), getId(SpinedKarok)))
    }

    "allow a creature with flying to block another creature with flying" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Seq(ConcordiaPegasus, SpinedKarok))
        .setBattlefield(playerTwo, Seq(AirElemental, AgelessGuardian, GnottvoldRecluse))

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, ConcordiaPegasus)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, AirElemental, ConcordiaPegasus)

      manager.currentAction must bePriorityChoice
    }

    "allow a creature with reach to block another creature with flying" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Seq(ConcordiaPegasus, SpinedKarok))
        .setBattlefield(playerTwo, Seq(AirElemental, AgelessGuardian, GnottvoldRecluse))

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, ConcordiaPegasus)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, GnottvoldRecluse, ConcordiaPegasus)

      manager.currentAction must bePriorityChoice
    }

    "not allow a creature without flying or reach to block a creature with flying" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Seq(ConcordiaPegasus, SpinedKarok))
        .setBattlefield(playerTwo, Seq(AirElemental, AgelessGuardian, GnottvoldRecluse))

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, ConcordiaPegasus)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)

      val stateBeforeBlock = manager.currentGameState
      manager.block(playerTwo, AgelessGuardian, ConcordiaPegasus)

      manager.currentGameState mustEqual stateBeforeBlock
    }
  }
}
