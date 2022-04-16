package mtg.misc

import mtg.core.symbols.ManaSymbol.{Blue, Red, White}
import mtg.game.objects.Card
import mtg.parts.costs.ManaCost
import mtg.{SpecWithGameStateManager, TestCardCreation}

class ManaValueSpec extends SpecWithGameStateManager with TestCardCreation {

  def cardWithManaCost(manaCost: ManaCost): Card = {
    Card(playerOne, getCardPrinting(creature(manaCost, Nil, Nil, (1, 1))))
  }

  "card with a generic mana cost" should {
    "have mana value equal to the required mana amount" in {
      cardWithManaCost(ManaCost(4)).baseCharacteristics.manaValue mustEqual 4
    }
  }

  "card with a single coloured mana symbol" should {
    "have mana value equal to 1" in {
      cardWithManaCost(ManaCost(White)).baseCharacteristics.manaValue mustEqual 1
    }
  }

  "card with multiple coloured mana symbols" should {
    "have mana value equal to the number of symbols" in {
      cardWithManaCost(ManaCost(White, Red, Blue)).baseCharacteristics.manaValue mustEqual 3
    }
  }

  "card with coloured and generic mana symbols in its cost" should {
    "have mana value equal to the total required mana" in {
      cardWithManaCost(ManaCost(2, White, Red)).baseCharacteristics.manaValue mustEqual 4
    }
  }
}
