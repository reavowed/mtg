package mtg.instructions

import mtg.abilities.builder.TypeConversions._
import mtg.definitions.types.Type.Creature
import mtg.game.turns.TurnPhase
import mtg.instructions.conditions.When
import mtg.instructions.nounPhrases.{CardName, Counters, It, Target}
import mtg.instructions.verbs.{Destroy, Die, Put}
import mtg.parts.Counter
import mtg.{SpecWithGameStateManager, TestCardCreation}

class PutCountersOnDeathSpec extends SpecWithGameStateManager with TestCardCreation {
  val CounterMovingCreature = zeroManaCreature(When(CardName, Die)(Put(Counters(It), Target(Creature))), (1, 1))
  val VanillaCreature = vanillaCreature(1, 1)
  val DestroySpell = simpleInstantSpell(Destroy(Target(Creature)))

  "creature that moves counters on death" should {
    "move counters on death" in {
      val initialState = emptyGameObjectState
        .addPermanentObject(playerOne, CounterMovingCreature, Map(Counter.PlusOnePlusOne -> 3))
        .addPermanentObject(playerOne, VanillaCreature)
        .setHand(playerOne, DestroySpell)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, DestroySpell)
      manager.chooseCard(playerOne, CounterMovingCreature)
      manager.resolveNext()
      manager.chooseCard(playerOne, VanillaCreature)
      manager.resolveNext()

      manager.getState(manager.getCard(VanillaCreature)).gameObject.counters(Counter.PlusOnePlusOne) mustEqual 3
    }
  }
}
