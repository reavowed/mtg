package mtg.abilities.keyword

import mtg.SpecWithGameStateManager
import mtg.core.zones.Zone
import mtg.data.cards.alpha.{LightningBolt, SavannahLions}
import mtg.data.cards.strixhaven.{AgelessGuardian, BeamingDefiance}
import mtg.data.cards.warofthespark.WardscaleCrocodile
import mtg.data.cards.{Mountain, Plains}
import mtg.game.turns.TurnPhase

class HexproofSpec extends SpecWithGameStateManager {
  "hexproof" should {
    "prevent a creature from being targeted" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, LightningBolt)
        .setBattlefield(playerOne, Mountain)
        .setBattlefield(playerTwo, Seq(WardscaleCrocodile, AgelessGuardian))

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.activateAbility(playerOne, Mountain)
      manager.castSpell(playerOne, LightningBolt)

      manager.currentChoice must beSome(beTargetChoice.forPlayer(playerOne).withAvailableTargets(AgelessGuardian, playerOne, playerTwo))
    }

    "prevent a spell from resolving if granted after cast" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, LightningBolt)
        .setBattlefield(playerOne, Mountain)
        .setHand(playerTwo, BeamingDefiance)
        .setBattlefield(playerTwo, Seq(Plains, Plains, SavannahLions))

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
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
