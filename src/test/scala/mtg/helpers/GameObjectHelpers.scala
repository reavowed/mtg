package mtg.helpers

import mtg.cards.{CardDefinition, CardPrinting}
import mtg.game.objects.{GameObject, PermanentObject}
import org.specs2.matcher.Matcher
import org.specs2.mutable.SpecificationLike

trait GameObjectHelpers extends SpecificationLike {
  def beTapped: Matcher[GameObject] = {
    ((_: GameObject).asOptionalInstanceOf[PermanentObject]) ^^ beSome(((_: PermanentObject).status.isTapped) ^^ beTrue)
  }

  implicit class GameObjectSeqExtensions(gameObjects: Seq[GameObject]) {
    def getCard(cardPrinting: CardPrinting): GameObject = {
      gameObjects.filter(_.card.printing == cardPrinting).single
    }
    def getCard(cardDefinition: CardDefinition): GameObject = {
      gameObjects.filter(_.card.printing.cardDefinition == cardDefinition).single
    }
  }
}
