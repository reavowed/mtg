package mtg.game.actions

import mtg.cards.patterns.Creature
import mtg.characteristics.Color
import mtg.data.cards.Plains
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.helpers.SpecWithTestCards
import mtg.parts.costs.ManaCost

class CastCreatureSpec extends SpecWithTestCards {
  val Creature = new Creature("Creature", ManaCost(), Nil, Nil, (1, 1))
  override def testCards = Seq(Creature)

  "casting a creature" should {


    "move the card to the battlefield on resolution" in {
      val initialState = emptyGameObjectState.setHand(playerOne, Seq(Creature))

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      manager.castSpell(playerOne, Creature)
      manager.passPriority(playerOne)
      manager.passPriority(playerTwo)

      manager.gameState.gameObjectState.stack must beEmpty
      manager.gameState.gameObjectState.battlefield must contain(beCardObject(Creature))
    }
  }

}
