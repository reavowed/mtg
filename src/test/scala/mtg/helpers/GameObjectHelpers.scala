package mtg.helpers

import mtg.cards.{CardDefinition, CardPrinting}
import mtg.game.objects.{Card, CardObject, GameObject}
import mtg.game.state.PermanentStatus
import org.specs2.matcher.Matcher
import org.specs2.mutable.SpecificationLike

trait GameObjectHelpers extends SpecificationLike {
  def beTapped: Matcher[GameObject] = {
    ((_: GameObject).permanentStatus) ^^ beSome(((_: PermanentStatus).isTapped) ^^ beTrue)
  }

  implicit class GameObjectExtensions(gameObject: GameObject) {
    def card: Card = gameObject.asInstanceOf[CardObject].card
  }

  implicit class GameObjectSeqExtensions(gameObjects: Seq[GameObject]) {
    def getCard(cardPrinting: CardPrinting): CardObject = {
      gameObjects.ofType[CardObject].filter(_.card.printing == cardPrinting).single
    }
    def getCard(cardDefinition: CardDefinition): CardObject = {
      gameObjects.ofType[CardObject].filter(_.card.printing.cardDefinition == cardDefinition).single
    }
  }
}
