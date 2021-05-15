package mtg.events

import mtg.SpecWithGameObjectState
import mtg.data.cards.{Forest, Plains}
import mtg.data.sets.Strixhaven
import mtg.game.objects.{Card, CardObject, GameObjectState, ObjectId}
import mtg.game.{GameData, PlayerIdentifier, Zone}

class DrawCardEventSpec extends SpecWithGameObjectState {
  "draw card event" should {
    val p1 = PlayerIdentifier("P1")
    val p2 = PlayerIdentifier("P2")

    val plains = Strixhaven.getCard(Plains).get
    val forest = Strixhaven.getCard(Forest).get

    "move the top card of the library to the hand" in {
      val gameData = GameData(Seq(p1, p2));
      val initialGameObjectState = GameObjectState(
        3,
        Map(p1 -> Seq(
          CardObject(Card(p1, plains), ObjectId(1), Zone.Library(p1)),
          CardObject(Card(p1, forest), ObjectId(2), Zone.Library(p1)))),
        Map(p1 -> Nil),
        Map(p1 -> Nil))
      val event = DrawCardEvent(p1)

      runEvent(event, gameData, initialGameObjectState).gameObjectState shouldEqual GameObjectState(
        4,
        Map(p1 -> Seq(
          CardObject(Card(p1, forest), ObjectId(2), Zone.Library(p1)))),
        Map(p1 -> Seq(
          CardObject(Card(p1, plains), ObjectId(3), Zone.Hand(p1)))),
        Map(p1 -> Nil))
    }

    "do nothing if library is empty" in {
      val gameData = GameData(Seq(p1, p2));
      val initialGameObjectState = GameObjectState(
        3,
        Map(p1 -> Nil),
        Map(p1 -> Nil),
        Map(p1 -> Nil))

      val event = DrawCardEvent(p1)

      runEvent(event, gameData, initialGameObjectState).gameObjectState shouldEqual initialGameObjectState
    }
  }
}
