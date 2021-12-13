package mtg.abilities.keyword

import mtg.SpecWithGameStateManager
import mtg.data.cards.alpha.AirElemental
import mtg.data.cards.kaldheim.GnottvoldRecluse
import mtg.data.cards.m21.ConcordiaPegasus
import mtg.data.cards.strixhaven.{AgelessGuardian, SpinedKarok}
import mtg.game.turns.TurnStep
import mtg.game.turns.turnBasedActions.DeclareBlockersChoice

class FlyingSpec extends SpecWithGameStateManager {
  "flying" should {
    "not offer blocks if defending player controls no creatures with flying or reach" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, ConcordiaPegasus)
        .setBattlefield(playerTwo, AgelessGuardian)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, ConcordiaPegasus)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)

      manager.currentChoice should beSome(bePriorityChoice)
    }

    "only offer blocks for an attacker with flying to creatures with flying or reach" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Seq(ConcordiaPegasus, SpinedKarok))
        .setBattlefield(playerTwo, Seq(AirElemental, AgelessGuardian, GnottvoldRecluse))

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, ConcordiaPegasus, SpinedKarok)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)

      manager.currentChoice must beSome(beAnInstanceOf[DeclareBlockersChoice])
      manager.currentChoice.get.asInstanceOf[DeclareBlockersChoice].possibleBlockers mustEqual Map(
        getId(AgelessGuardian) -> Seq(getId(SpinedKarok)),
        getId(AirElemental) -> Seq(getId(ConcordiaPegasus), getId(SpinedKarok)),
        getId(GnottvoldRecluse) -> Seq(getId(ConcordiaPegasus), getId(SpinedKarok)))
    }

    "allow a creature with flying to block another creature with flying" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Seq(ConcordiaPegasus, SpinedKarok))
        .setBattlefield(playerTwo, Seq(AirElemental, AgelessGuardian, GnottvoldRecluse))

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, ConcordiaPegasus)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, AirElemental, ConcordiaPegasus)

      manager.currentChoice must beSome(bePriorityChoice)
    }

    "allow a creature with reach to block another creature with flying" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Seq(ConcordiaPegasus, SpinedKarok))
        .setBattlefield(playerTwo, Seq(AirElemental, AgelessGuardian, GnottvoldRecluse))

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, ConcordiaPegasus)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(playerTwo, GnottvoldRecluse, ConcordiaPegasus)

      manager.currentChoice must beSome(bePriorityChoice)
    }

    "not allow a creature without flying or reach to block a creature with flying" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Seq(ConcordiaPegasus, SpinedKarok))
        .setBattlefield(playerTwo, Seq(AirElemental, AgelessGuardian, GnottvoldRecluse))

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(playerOne, ConcordiaPegasus)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)

      val stateBeforeBlock = manager.gameState
      manager.block(playerTwo, AgelessGuardian, ConcordiaPegasus)

      manager.gameState mustEqual stateBeforeBlock
    }
  }
}
