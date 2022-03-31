package mtg.sets.strixhaven.cards

import mtg.SpecWithGameStateManager
import mtg.sets.alpha.cards.{Plains, SavannahLions}

class BeamingDefianceSpec extends SpecWithGameStateManager {
  "Beaming Defiance" should {
    "only target controller's creature" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, BeamingDefiance)
        .setBattlefield(playerOne, SavannahLions, Plains, Plains)
        .setBattlefield(playerTwo, AgelessGuardian)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.activateAbilities(playerOne, Plains, 2)
      manager.castSpell(playerOne, BeamingDefiance)

      manager.currentChoice must beSome(beTargetChoice.forPlayer(playerOne).withAvailableTargets(SavannahLions))
    }

    "grant p/t bonus" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, BeamingDefiance)
        .setBattlefield(playerOne, SavannahLions, Plains, Plains)
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
