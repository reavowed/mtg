package mtg.stateBasedActions

import mtg.{SpecWithGameStateManager, TestCardCreation}
import mtg.core.zones.Zone
import mtg.game.turns.TurnStep

class LethalDamageStateBasedActionSpec extends SpecWithGameStateManager with TestCardCreation {
  val VanillaOneOne = vanillaCreature(1, 1)
  val VanillaTwoTwo = vanillaCreature(2, 2)

  "SBA for lethal damage" should {
    "put a creature dealt lethal combat damage in the graveyard" in {
      val initialState = gameObjectStateWithInitialLibrariesAndHands
        .setBattlefield(playerOne, VanillaTwoTwo)
        .setBattlefield(playerTwo, VanillaOneOne)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)

      manager.passUntilStep(TurnStep.DeclareAttackersStep)
      manager.attackWith(VanillaTwoTwo)
      manager.passUntilStep(TurnStep.DeclareBlockersStep)
      manager.block(VanillaOneOne, VanillaTwoTwo)
      manager.passUntilStep(TurnStep.CombatDamageStep)

      Zone.Battlefield(manager.gameState) must not(contain(beCardObject(VanillaOneOne)))
      Zone.Graveyard(playerOne)(manager.gameState) must beEmpty
      Zone.Graveyard(playerTwo)(manager.gameState) must contain(exactly(beCardObject(VanillaOneOne)))
    }
  }
}
