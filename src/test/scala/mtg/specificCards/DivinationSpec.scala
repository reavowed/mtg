package mtg.specificCards

import mtg.SpecWithGameStateManager
import mtg.data.cards.{Forest, Island, Mountain}
import mtg.data.cards.alpha.{LightningBolt, SavannahLions}
import mtg.data.cards.kaldheim.GrizzledOutrider
import mtg.data.cards.m10.Divination
import mtg.game.Zone
import mtg.game.turns.StartNextTurnAction
import mtg.game.turns.TurnPhase.PrecombatMainPhase

class DivinationSpec extends SpecWithGameStateManager {
  "Divination" should {
    "draw two cards" in {
      val initialState = emptyGameObjectState
        .setLibrary(playerOne, Forest, 3)
        .setBattlefield(playerOne, Island, 3)
        .setHand(playerOne, Divination)

      val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.passUntilPhase(PrecombatMainPhase)
      manager.activateAbilities(playerOne, Island, 3)
      manager.castSpell(playerOne, Divination)

      val stateBeforeResolving = manager.currentGameState
      manager.resolveNext()

      playerOne.hand(manager.currentGameState).size must beEqualTo(playerOne.hand(stateBeforeResolving).size + 2)
    }
  }
}
