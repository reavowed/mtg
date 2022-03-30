package mtg.abilities.keyword

import mtg.abilities.builder.InstructionBuilder.endOfTurn
import mtg.abilities.builder.TypeConversions._
import mtg.core.types.Type.Creature
import mtg.core.zones.Zone
import mtg.game.turns.TurnPhase
import mtg.instructions.nounPhrases.Target
import mtg.instructions.verbs.{Destroy, Gain}
import mtg.{SpecWithGameStateManager, TestCardCreation}

class HexproofSpec extends SpecWithGameStateManager with TestCardCreation {
  val CreatureWithHexproof = zeroManaCreature(Hexproof, (1, 1))
  val VanillaCreature = vanillaCreature(1, 1)
  val TargetedSpell = simpleInstantSpell(Destroy(Target(Creature)))
  val SpellGrantingHexproof = simpleInstantSpell(Target(Creature)(Gain(Hexproof), endOfTurn))

  "hexproof" should {
    "prevent a creature from being targeted" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TargetedSpell)
        .setBattlefield(playerTwo, CreatureWithHexproof, VanillaCreature)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.castSpell(playerOne, TargetedSpell)

      manager.currentChoice must beSome(beTargetChoice.forPlayer(playerOne).withAvailableTargets(VanillaCreature))
    }

    "prevent a spell from resolving if granted after cast" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, TargetedSpell)
        .setHand(playerTwo, SpellGrantingHexproof)
        .setBattlefield(playerTwo, VanillaCreature)

      implicit val manager = createGameStateManagerAtStartOfFirstTurn(initialState)
      manager.passUntilPhase(TurnPhase.PrecombatMainPhase)
      manager.castSpell(playerOne, TargetedSpell)
      manager.chooseCard(playerOne, VanillaCreature)
      manager.passPriority(playerOne)
      manager.castSpell(playerTwo, SpellGrantingHexproof)
      manager.chooseCard(playerTwo, VanillaCreature)
      manager.passUntilStackEmpty()

      manager.getCard(VanillaCreature).zone mustEqual Zone.Battlefield
    }

    // TODO: hexproof on players
  }
}
