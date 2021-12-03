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

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.activateAbilities(playerOne, Plains, 2)
      manager.castSpell(playerOne, BeamingDefiance)

      manager.currentChoice must beSome(beTargetChoice.forPlayer(playerOne).withAvailableTargets(SavannahLions))
    }

    "grant p/t bonus" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, BeamingDefiance)
        .setBattlefield(playerOne, Seq(SavannahLions, Plains, Plains))
        .setBattlefield(playerTwo, AgelessGuardian)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.activateAbilities(playerOne, Plains, 2)
      manager.castSpell(playerOne, BeamingDefiance)
      manager.chooseCard(playerOne, SavannahLions)
      manager.resolveNext()

      manager.getState(manager.getCard(SavannahLions)).characteristics.power must beSome(4)
      manager.getState(manager.getCard(SavannahLions)).characteristics.toughness must beSome(3)
    }
  }
}
