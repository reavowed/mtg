package mtg.abilities.keyword

import mtg.SpecWithGameStateManager
import mtg.data.cards.Mountain
import mtg.data.cards.alpha.LightningBolt
import mtg.data.cards.strixhaven.AgelessGuardian
import mtg.data.cards.warofthespark.WardscaleCrocodile
import mtg.game.turns.StartNextTurnAction

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
    // TODO: prevent spell from resolving if granted after cast
    // TODO: hexproof on players
  }
}
