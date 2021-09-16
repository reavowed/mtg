package mtg.templates

import mtg.SpecWithGameStateManager
import mtg.abilities.builder.EffectBuilder._
import mtg.cards.patterns.Spell
import mtg.cards.{CardDefinition, CardPrinting}
import mtg.characteristics.types.Type
import mtg.data.cards.Plains
import mtg.data.sets.Strixhaven
import mtg.game.turns.StartNextTurnAction
import mtg.parts.costs.ManaCost
import mtg.stack.adding.ModeChoice

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

  override def getCardPrinting(cardDefinition: CardDefinition): CardPrinting = {
    if (cardDefinition == ModalCard)
      CardPrinting(cardDefinition, Strixhaven, 999)
    else
      super.getCardPrinting(cardDefinition)
  }

  "modal card" should {
    "have correct oracle text" in {
      ModalCard.text mustEqual
        """Choose one —
          |• You gain 3 life.
          |• Draw a card.""".stripMargin.replace("\r", "")
    }

    "require a choice on casting" in {
      val initialState = emptyGameObjectState
        .setHand(playerOne, ModalCard)
        .setBattlefield(playerOne, Plains)

      implicit val manager = createGameStateManager(initialState, StartNextTurnAction(playerOne))
      manager.activateAbility(playerOne, Plains)
      manager.castSpell(playerOne, ModalCard)

      manager.currentAction must beAnInstanceOf[ModeChoice]
    }
  }
}
