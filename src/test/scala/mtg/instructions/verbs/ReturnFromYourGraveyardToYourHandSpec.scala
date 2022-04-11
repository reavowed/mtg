package mtg.instructions.verbs

import mtg.abilities.builder.InstructionBuilder._
import mtg.abilities.builder.TypeConversions._
import mtg.core.types.Type.Creature
import mtg.instructions.nounPhrases.{CardName, Target}
import mtg.instructions.nouns.Card
import mtg.parts.costs.ManaCost
import mtg.{SpecWithGameStateManager, TestCardCreation}

class ReturnFromYourGraveyardToYourHandSpec extends SpecWithGameStateManager with TestCardCreation {
  val CreatureWithSelfReturnAbility = zeroManaCreature(
    activatedAbility(ManaCost(0))(ReturnFromYourGraveyardToYourHand(CardName)),
    (1, 1))
  val CreatureWithTargetedReturnAbility = zeroManaCreature(
    activatedAbility(ManaCost(0))(ReturnFromYourGraveyardToYourHand(Target(Creature(Card)))),
    (1, 1))

  "an ability that returns the card it is on from your graveyard to your hand" should {
    "have the correct oracle text" in {
      CreatureWithSelfReturnAbility.text mustEqual s"{0}: Return ${CreatureWithSelfReturnAbility.name} from your graveyard to your hand."
    }
  }

  "an ability that returns a different card from your graveyard to your hand" should {
    "have the correct oracle text" in {
      CreatureWithTargetedReturnAbility.text mustEqual s"{0}: Return target creature card from your graveyard to your hand."
    }
  }
}
