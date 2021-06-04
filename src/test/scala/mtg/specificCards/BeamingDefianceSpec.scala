package mtg.specificCards

import mtg.SpecWithGameStateManager
import mtg.data.cards.{Mountain, Plains}
import mtg.data.cards.alpha.{LightningBolt, SavannahLions}
import mtg.data.cards.kaldheim.GrizzledOutrider
import mtg.data.cards.strixhaven.{AgelessGuardian, BeamingDefiance}
import mtg.game.Zone
import mtg.game.turns.StartNextTurnAction

class BeamingDefianceSpec extends SpecWithGameStateManager {
  "Beaming Defiance" should {
    "only target controller's creature" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, BeamingDefiance)
        .setBattlefield(playerOne, Seq(SavannahLions, Plains, Plains))
        .setBattlefield(playerTwo, AgelessGuardian)

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.activateAbilities(playerOne, Plains, 2)
      manager.castSpell(playerOne, BeamingDefiance)

      manager.currentAction must beTargetChoice.forPlayer(playerOne).withAvailableTargets(SavannahLions)
    }
  }
}
