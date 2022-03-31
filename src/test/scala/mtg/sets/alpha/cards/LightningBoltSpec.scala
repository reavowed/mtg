package mtg.sets.alpha.cards

import mtg.SpecWithGameStateManager
import mtg.core.zones.Zone
import mtg.sets.kaldheim.cards.GrizzledOutrider

class LightningBoltSpec extends SpecWithGameStateManager {
  "Lightning Bolt" should {
    "kill a small creature" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Mountain)
        .setHand(playerOne, LightningBolt)
        .setBattlefield(playerTwo, SavannahLions)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.activateAbility(playerOne, Mountain)
      manager.castSpell(playerOne, LightningBolt)
      manager.chooseCard(playerOne, SavannahLions)
      manager.resolveNext()

      Zone.Battlefield(manager.gameState) must not(contain(beCardObject(SavannahLions)))
      playerTwo.graveyard(manager.gameState) must contain(beCardObject(SavannahLions))
    }
    "deal three damage to a big creature" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Mountain)
        .setHand(playerOne, LightningBolt)
        .setBattlefield(playerTwo, GrizzledOutrider)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.activateAbility(playerOne, Mountain)
      manager.castSpell(playerOne, LightningBolt)
      manager.chooseCard(playerOne, GrizzledOutrider)
      manager.resolveNext()

      manager.getPermanent(GrizzledOutrider).markedDamage mustEqual 3
    }
    "deal three damage to a player" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, Mountain)
        .setHand(playerOne, LightningBolt)
        .setBattlefield(playerTwo, GrizzledOutrider)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.activateAbility(playerOne, Mountain)
      manager.castSpell(playerOne, LightningBolt)
      manager.choosePlayer(playerOne, playerTwo)
      manager.resolveNext()

      manager.gameState.gameObjectState.lifeTotals(playerTwo) mustEqual 17
    }

    "have correct oracle text" in {
      LightningBolt.text mustEqual "Lightning Bolt deals 3 damage to any target."
    }
  }
}
