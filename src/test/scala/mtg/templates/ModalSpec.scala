package mtg.templates

import mtg.abilities.builder.EffectBuilder._
import mtg.SpecWithGameStateManager
import mtg.cards.patterns.Spell
import mtg.characteristics.types.Type
import mtg.parts.costs.ManaCost

class ModalSpec extends SpecWithGameStateManager {
  object ModalCard extends Spell(
    "Modal Card",
    ManaCost(1),
    Type.Instant,
    Nil,
    chooseOne(
      you.gain(3).life,
      drawACard)
  )

  "modal card" should {
    "have correct oracle text" in {
      ModalCard.text mustEqual
        """Choose one —
          |• You gain 3 life.
          |• Draw a card.""".stripMargin.replace("\r", "")
    }
  }
}
