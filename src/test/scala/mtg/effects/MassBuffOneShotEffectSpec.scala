package mtg.effects

import mtg.SpecWithGameStateManager
import mtg.abilities.builder.EffectBuilder._
import mtg.cards.{CardDefinition, CardPrinting}
import mtg.cards.patterns.Spell
import mtg.characteristics.types.Type
import mtg.characteristics.types.Type.Creature
import mtg.data.sets.Strixhaven
import mtg.parts.costs.ManaCost

class MassBuffOneShotEffectSpec extends SpecWithGameStateManager {
  object Card extends Spell(
    "Card",
    ManaCost(0),
    Type.Instant,
    Nil,
    Creature(you.control)(get(1, 1)).until(endOfTurn)
  )

  override def getCardPrinting(cardDefinition: CardDefinition): CardPrinting = {
    if (cardDefinition == Card)
      CardPrinting(cardDefinition, Strixhaven, 999)
    else
      super.getCardPrinting(cardDefinition)
  }

  "mass buff effect" should {
    "have correct oracle text" in {
      Card.text mustEqual "Creatures you control get +1/+1 until end of turn."
    }
  }
}
