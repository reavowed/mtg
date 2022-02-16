package mtg.helpers

import mtg.cards.CardDefinition
import mtg.game.objects.{Card, GameObject, PermanentObject, UnderlyingObject}
import mtg.game.state.{Characteristics, GameState}
import org.specs2.matcher.Matcher
import org.specs2.mutable.SpecificationLike

import scala.collection.View

trait GameObjectHelpers extends SpecificationLike {
  def beObject(underlyingObject: UnderlyingObject): Matcher[GameObject] = { (gameObject: GameObject) =>
    (gameObject.underlyingObject == underlyingObject, "", "")
  }
  def beCardObject(cardDefinition: CardDefinition): Matcher[GameObject] = { (gameObject: GameObject) =>
    (gameObject.underlyingObject.asOptionalInstanceOf[Card].exists(_.printing.cardDefinition == cardDefinition), "", "")
  }

  def beTapped: Matcher[GameObject] = {
    ((_: GameObject).asOptionalInstanceOf[PermanentObject]) ^^ beSome(((_: PermanentObject).status.isTapped) ^^ beTrue)
  }

  def havePowerAndToughness(power: Int, toughness: Int)(implicit gameState: GameState): Matcher[PermanentObject] = {
    ((o: PermanentObject) => gameState.gameObjectState.derivedState.permanentStates(o.objectId).characteristics) ^^ (
      (((_: Characteristics).power) ^^ beSome(power)) and (((_: Characteristics).toughness) ^^ beSome(toughness))
    )
  }

  implicit class GameObjectExtensions[T <: GameObject](gameObject: T) {
    def isCard(cardDefinition: CardDefinition): Boolean = gameObject.underlyingObject.asOptionalInstanceOf[Card].exists(_.printing.cardDefinition == cardDefinition)
  }
  implicit class GameObjectSeqExtensions[T <: GameObject](gameObjects: Seq[T]) {
    def getCard(cardDefinition: CardDefinition): T = {
      gameObjects.view.getCard(cardDefinition)
    }
    def getMatching(predicate: T => Boolean): T = gameObjects.view.getMatching(predicate)
  }
  implicit class GameObjectViewExtensions[T <: GameObject](gameObjects: View[T]) {
    def getMatching(predicate: T => Boolean): T = gameObjects.filter(predicate).single
    def getCard(cardDefinition: CardDefinition): T = getMatching(_.isCard(cardDefinition))
  }
}
