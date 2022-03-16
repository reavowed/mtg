package mtg.abilities

import mtg.abilities.builder.InstructionBuilder._
import mtg.cards.patterns.Creature
import mtg.cards.text.SimpleInstructionParagraph
import mtg.core.ManaType
import mtg.core.symbols.ManaSymbol.{Green, Red}
import mtg.data.cards.Plains
import mtg.game.turns.TurnPhase
import mtg.helpers.SpecWithTestCards
import mtg.instructions.actions.DrawACard
import mtg.parts.costs.{ManaCost, TapSymbol}

class ManaAbilitySpec extends SpecWithTestCards {

  val CreatureWithManaAbility = new Creature(
    "Creature with Mana Ability",
    ManaCost(0),
    Nil,
    Seq(activatedAbility(TapSymbol)(add(Green))),
    (1, 1))
  val CreatureWithAbilityThatDrawsACard = new Creature(
    "Creature with Ability that Doesn't Add Mana",
    ManaCost(0),
    Nil,
    Seq(activatedAbility(TapSymbol)(DrawACard)),
    (1, 1))
  val CreatureWithAbilityThatAddsManaButTargets = new Creature(
    "Creature with Ability that Adds Mana but Targets",
    ManaCost(0),
    Nil,
    Seq(activatedAbility(TapSymbol)(SimpleInstructionParagraph(cardName.deals(1).damageTo(anyTarget), add(Red)))),
    (1, 1))

  override def testCards = Seq(CreatureWithManaAbility, CreatureWithAbilityThatDrawsACard, CreatureWithAbilityThatAddsManaButTargets)

  "mana ability" >> {
    "on land" should {
      "not use the stack" in {
        val initialState = emptyGameObjectState
          .setBattlefield(playerOne, Plains)

        implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
        manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
        manager.activateAbility(playerOne, Plains)

        manager.gameState.gameObjectState.stack must beEmpty
        manager.gameState.gameObjectState.manaPools(playerOne).map(_.manaType) must contain(exactly[ManaType](ManaType.White))
      }
    }
    "on creature" should {
      "not use the stack" in {
        val initialState = emptyGameObjectState
          .setBattlefield(playerOne, CreatureWithManaAbility)

        implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
        manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
        manager.activateAbility(playerOne, CreatureWithManaAbility)

        manager.gameState.gameObjectState.stack must beEmpty
        manager.gameState.gameObjectState.manaPools(playerOne).map(_.manaType) must contain(exactly[ManaType](ManaType.Green))
      }
    }
  }

  "ability that doesn't add mana" should {
    "use the stack" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, CreatureWithAbilityThatDrawsACard)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.activateAbility(playerOne, CreatureWithAbilityThatDrawsACard)

      manager.gameState.gameObjectState.stack must contain(exactly(beActivatedAbilityOf(CreatureWithAbilityThatDrawsACard)))
      playerOne.hand(manager.gameState).size mustEqual playerOne.hand(initialState).size
    }
  }

  "ability that adds mana but targets" should {
    "use the stack" in {
      val initialState = emptyGameObjectState
        .setBattlefield(playerOne, CreatureWithAbilityThatAddsManaButTargets)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.activateAbility(playerOne, CreatureWithAbilityThatAddsManaButTargets)

      manager.gameState.gameObjectState.stack must contain(exactly(beActivatedAbilityOf(CreatureWithAbilityThatAddsManaButTargets)))
      playerOne.hand(manager.gameState).size mustEqual playerOne.hand(initialState).size
    }
  }
}
