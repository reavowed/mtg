package mtg.instructions.verbs

import mtg.abilities.builder.TypeConversions._
import mtg.core.types.Type.Creature
import mtg.core.zones.Zone
import mtg.game.turns.TurnPhase
import mtg.instructions.nounPhrases.Target
import mtg.{SpecWithGameStateManager, TestCardCreation}

class DestroySpec extends SpecWithGameStateManager with TestCardCreation {
  val DestroySpell = simpleInstantSpell(Destroy(Target(Creature)))
  val VanillaOneOne = vanillaCreature(1, 1)

  "destroy effect" should {
    "have correct oracle text" in {
      DestroySpell.text mustEqual "Destroy target creature."
    }

    "move the creature to the graveyard" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, DestroySpell)
        .setBattlefield(playerTwo, VanillaOneOne)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, DestroySpell)
      manager.chooseCard(playerOne, VanillaOneOne)
      manager.resolveNext()

      Zone.Battlefield(manager.gameState) must beEmpty
      playerTwo.graveyard(manager.gameState) must contain(exactly(beCardObject(VanillaOneOne)))
    }
  }
}
