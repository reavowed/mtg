package mtg.events

import mtg.data.cards.{Forest, Plains}
import mtg.data.sets.Strixhaven
import mtg.game.{GameData, PlayerIdentifier}
import mtg.game.objects.{Card, CardObject, GameObjectState, ObjectId}
import mtg.game.state.{GameResult, GameState, GameStateManager, HandleEventsAction}
import mtg.game.zone.{Zone, ZoneState}
import org.specs2.mutable.Specification

class DrawCardEventSpec extends Specification {
  "draw card event" should {
    val p1 = PlayerIdentifier("P1")
    val p2 = PlayerIdentifier("P2")

    val plains = Strixhaven.getCard(Plains).get
    val forest = Strixhaven.getCard(Forest).get

    def runEvent(event: Event, gameData: GameData, gameObjectState: GameObjectState): GameObjectState = {
      new GameStateManager(GameState(gameData, gameObjectState, HandleEventsAction(Seq(event), GameResult.Tie))).gameState.gameObjectState
    }

    "move the top card of the library to the hand" in {
      val gameData = GameData(Seq(p1, p2));
      val initialGameObjectState = GameObjectState(
        3,
        Map(p1 -> ZoneState(Seq(
          CardObject(Card(p1, plains), ObjectId(1), Zone.Library(p1)),
          CardObject(Card(p1, forest), ObjectId(2), Zone.Library(p1))))),
        Map(p1 -> ZoneState(Nil)),
        Map(p1 -> ZoneState(Nil)))
      val event = DrawCardEvent(p1)

      runEvent(event, gameData, initialGameObjectState) shouldEqual GameObjectState(
        4,
        Map(p1 -> ZoneState(Seq(
          CardObject(Card(p1, forest), ObjectId(2), Zone.Library(p1))))),
        Map(p1 -> ZoneState(Seq(
          CardObject(Card(p1, plains), ObjectId(3), Zone.Hand(p1))))),
        Map(p1 -> ZoneState(Nil)))
    }

    "do nothing if library is empty" in {
      val gameData = GameData(Seq(p1, p2));
      val initialGameObjectState = GameObjectState(
        3,
        Map(p1 -> ZoneState(Nil)),
        Map(p1 -> ZoneState(Nil)),
        Map(p1 -> ZoneState(Nil)))

      val event = DrawCardEvent(p1)

      runEvent(event, gameData, initialGameObjectState) shouldEqual initialGameObjectState
    }
  }
}
