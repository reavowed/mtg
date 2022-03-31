package mtg.game.actions

import mtg.core.ManaType
import mtg.core.symbols.ManaSymbol
import mtg.core.symbols.ManaSymbol.White
import mtg.game.state.Choice
import mtg.game.turns.TurnPhase.PrecombatMainPhase
import mtg.parts.costs.ManaCost
import mtg.sets.alpha.cards.Plains
import mtg.stack.adding.PayManaChoice
import mtg.{SpecWithGameStateManager, TestCardCreation}

class CastSpellManaPaymentSpec extends SpecWithGameStateManager with TestCardCreation {
  val WhiteCreature = vanillaCreature(ManaCost(White), (1, 1))
  val FourCreature = vanillaCreature(ManaCost(4), (1, 1))

  "casting a creature that costs {W}" should {
    "offer payment choice with no mana in pool" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, WhiteCreature)
        .setBattlefield(playerOne, Plains)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      manager.castSpell(playerOne, WhiteCreature)

      manager.currentChoice must beSome(beInstanceThat[PayManaChoice](((_: PayManaChoice).remainingCost.symbols) ^^ contain(exactly[ManaSymbol](ManaSymbol.White))))
    }

    "autopay mana added while paying costs" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, WhiteCreature)
        .setBattlefield(playerOne, Plains)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      manager.castSpell(playerOne, WhiteCreature)
      manager.activateAbility(playerOne, Plains)

      manager.gameState.gameObjectState.manaPools(playerOne) must beEmpty
      manager.gameState.gameObjectState.hands(playerOne) must beEmpty
      manager.gameState.gameObjectState.stack must contain(exactly(beCardObject(WhiteCreature)))
      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne))

    }

    "autopay full cost with correct mana in pool on casting" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, WhiteCreature)
        .setBattlefield(playerOne, Plains)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      // Add necessary mana
      manager.activateAbility(playerOne, Plains)
      manager.gameState.gameObjectState.manaPools(playerOne).map(_.manaType) must contain(exactly[ManaType](ManaType.White))

      // Cast spell
      manager.castSpell(playerOne, WhiteCreature)

      manager.gameState.gameObjectState.manaPools(playerOne) must beEmpty
      manager.gameState.gameObjectState.hands(playerOne) must beEmpty
      manager.gameState.gameObjectState.stack must contain(exactly(beCardObject(WhiteCreature)))
      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne))
    }
  }

  "casting a creature that costs {4}" should {
    "auto cast with four mana in pool" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, FourCreature)
        .setBattlefield(playerOne, Plains, 4)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      manager.activateAbilities(playerOne, Plains, 4)
      manager.castSpell(playerOne, FourCreature)

      manager.gameState.gameObjectState.manaPools(playerOne) must beEmpty
      manager.gameState.gameObjectState.hands(playerOne) must beEmpty
      manager.gameState.gameObjectState.stack must contain(exactly(beCardObject(FourCreature)))
      manager.currentChoice must beSome(bePriorityChoice.forPlayer(playerOne))
    }

    "partially pay with two mana in pool" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, FourCreature)
        .setBattlefield(playerOne, Plains, 4)

      val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(PrecombatMainPhase)

      manager.activateAbilities(playerOne, Plains, 2)
      manager.castSpell(playerOne, FourCreature)

      manager.gameState.gameObjectState.manaPools(playerOne) must beEmpty
      manager.currentChoice must beSome(beAnInstanceOf[PayManaChoice] and ((_: Choice[_]).asInstanceOf[PayManaChoice].remainingCost.symbols) ^^ contain(exactly[ManaSymbol](ManaSymbol.Generic(2))))
    }
  }
}
