package mtg.turns

import mtg.SpecWithGameStateManager
import mtg.data.cards.{Forest, Plains}
import mtg.game.Zone
import mtg.game.turns.TurnPhase.PrecombatMainPhase

class UntapStepSpec extends SpecWithGameStateManager {
  "untap step" should {
    "untap only tapped permanents the active player controls" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands.setHand(playerOne, Seq(Plains)).setHand(playerTwo, Seq(Forest))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)
      manager.playLand(playerOne, Plains)
      manager.activateAbility(playerOne, Plains)
      manager.passUntilTurnAndPhase(2, PrecombatMainPhase)
      manager.playLand(playerTwo, Forest)
      manager.activateAbility(playerTwo, Forest)
      manager.passUntilTurnAndPhase(3, PrecombatMainPhase)

      manager.getCard(Zone.Battlefield, Plains) must not(beTapped)
      manager.getCard(Zone.Battlefield, Forest) must beTapped
    }
  }

}
