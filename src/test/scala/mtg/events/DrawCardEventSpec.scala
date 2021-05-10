package mtg.events

import mtg.data.cards.{Forest, Plains}
import mtg.data.sets.Strixhaven
import mtg.game.`object`.{Card, CardObject, ObjectId}
import mtg.game.loop.GameLoop
import mtg.game.zone.{Hand, Library, ZoneState, ZoneStates}
import mtg.game.{GameState, PlayerIdentifier, Sideboard}
import org.specs2.mutable.Specification

class DrawCardEventSpec extends Specification {
  "draw card event" should {
    val p1 = PlayerIdentifier("P1")
    val p2 = PlayerIdentifier("P2")

    val plains = Strixhaven.getCard(Plains).get
    val forest = Strixhaven.getCard(Forest).get

    "move the top card of the library to the hand" in {
      val initialGameState = GameState(
        3,
        Seq(p1, p2),
        ZoneStates(
          Map(p1 -> ZoneState(Seq(
            CardObject(Card(p1, plains), ObjectId(1), Library(p1)),
            CardObject(Card(p1, forest), ObjectId(2), Library(p1))))),
          Map(p1 -> ZoneState(Nil))),
        Map(p1 -> Sideboard(Nil)))

      val event = DrawCardEvent(p1)

      GameLoop.resolveEvent(event, initialGameState) shouldEqual GameState(
        4,
        Seq(p1, p2),
        ZoneStates(
          Map(p1 -> ZoneState(Seq(
            CardObject(Card(p1, forest), ObjectId(2), Library(p1))))),
          Map(p1 -> ZoneState(Seq(
            CardObject(Card(p1, plains), ObjectId(3), Hand(p1)))))),
        Map(p1 -> Sideboard(Nil)))
    }

    "do nothing if library is empty" in {
      val initialGameState = GameState(
        3,
        Seq(p1, p2),
        ZoneStates(
          Map(p1 -> ZoneState(Nil)),
          Map(p1 -> ZoneState(Nil))),
        Map(p1 -> Sideboard(Nil)))

      val event = DrawCardEvent(p1)

      GameLoop.resolveEvent(event, initialGameState) shouldEqual initialGameState
    }
  }
}
