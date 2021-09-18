package mtg.abilities.keyword

import mtg.SpecWithGameStateManager
import mtg.data.cards.{Mountain, Plains}
import mtg.data.cards.alpha.{LightningBolt, SavannahLions}
import mtg.data.cards.kaldheim.GrizzledOutrider
import mtg.data.cards.strixhaven.{AgelessGuardian, BeamingDefiance, IntroductionToAnnihilation}
import mtg.data.cards.warofthespark.WardscaleCrocodile
import mtg.game.Zone
import mtg.game.turns.{StartNextTurnAction, TurnPhase}

class HexproofSpec extends SpecWithGameStateManager {
  "hexproof" should {
    "prevent a creature from being targeted" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, LightningBolt)
        .setBattlefield(playerOne, Mountain)
        .setBattlefield(playerTwo, Seq(WardscaleCrocodile, AgelessGuardian))

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.activateAbility(playerOne, Mountain)
      manager.castSpell(playerOne, LightningBolt)

      manager.currentAction must beTargetChoice.forPlayer(playerOne).withAvailableTargets(AgelessGuardian, playerOne, playerTwo)
    }

    "prevent a spell from resolving if granted after cast" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, LightningBolt)
        .setBattlefield(playerOne, Mountain)
        .setHand(playerTwo, BeamingDefiance)
        .setBattlefield(playerTwo, Seq(Plains, Plains, SavannahLions))

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.activateAbility(playerOne, Mountain)
      manager.castSpell(playerOne, LightningBolt)
      manager.chooseCard(playerOne, SavannahLions)
      manager.passPriority(playerOne)
      manager.activateAbilities(playerTwo, Plains, 2)
      manager.castSpell(playerTwo, BeamingDefiance)
      manager.chooseCard(playerTwo, SavannahLions)
      manager.passUntilStackEmpty()

      manager.getCard(SavannahLions).zone mustEqual Zone.Battlefield
      manager.getPermanent(SavannahLions).markedDamage mustEqual 0
    }

    // TODO: hexproof on players
  }
}