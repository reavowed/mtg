package mtg.instructions.verbs

import mtg.definitions.zones.Zone
import mtg.definitions.{ObjectId, PlayerId}
import mtg.effects.InstructionResolutionContext
import mtg.game.state.history.LogEvent
import mtg.game.state.{Choice, DirectGameObjectAction, GameState}
import mtg.instructions._
import mtg.instructions.grammar.VerbInflection
import mtg.utils.ParsingUtils

case class Scry(number: Int) extends IntransitiveInstructionVerb[PlayerId] with Verb.RegularCaseObject {
  override def thirdPerson: String = "scries"
  override def inflect(verbInflection: VerbInflection, cardName: String): String = {
    super.inflect(verbInflection, cardName) + " " + number
  }
  override def resolve(playerId: PlayerId): InstructionAction = InstructionAction.withoutContextUpdate { gameState =>
    val library = gameState.gameObjectState.libraries(playerId)
    val cardsBeingScryed = library.take(number).map(_.objectId)
    ScryChoice(playerId, cardsBeingScryed).flatMap {
      case ScryDecision(cardsOnTop, cardsOnBottom) =>
        ScryAction(playerId, cardsOnTop, cardsOnBottom)
    }
  }
}

case class ScryDecision(cardsOnTop: Seq[ObjectId], cardsOnBottom: Seq[ObjectId])

case class ScryChoice(
    playerToAct: PlayerId,
    cardsBeingScried: Seq[ObjectId])
  extends Choice[ScryDecision]
{
  override def handleDecision(
    serializedDecision: String)(
    implicit gameState: GameState
  ): Option[ScryDecision] = {
    for {
      (serializedCardsOnTop, serializedCardsOnBottom) <- serializedDecision.split("\\|", -1).toSeq match {
        case Seq(a, b) => Some(a, b)
        case _ => None
      }
      cardsOnTop <- ParsingUtils.splitStringAsIds(serializedCardsOnTop)
      cardsOnBottom <- ParsingUtils.splitStringAsIds(serializedCardsOnBottom)
      if (cardsOnTop ++ cardsOnBottom).toSet == cardsBeingScried.toSet
    } yield ScryDecision(cardsOnTop, cardsOnBottom)
  }

  override def temporarilyVisibleObjects: Seq[ObjectId] = cardsBeingScried
}

case class ScryAction(
    player: PlayerId,
    cardsOnTop: Seq[ObjectId],
    cardsOnBottom: Seq[ObjectId])
  extends DirectGameObjectAction[Unit]
{
  override def execute(implicit gameState: GameState): DirectGameObjectAction.Result[Unit] = {
    // TODO: Should create new game objects to hide card identities (if scrying more than one)
    gameState.gameObjectState.updateZone(Zone.Library(player), library => {
      val onTop = cardsOnTop.map(id => library.find(_.objectId == id).get)
      val onBottom = cardsOnBottom.map(id => library.find(_.objectId == id).get)
      onTop ++ library.filter(c => !(cardsOnTop ++ cardsOnBottom).contains(c.objectId)) ++ onBottom
    })
  }
  // TODO: Theoretically possible to revert if scrying player already knew the identity of the scryed cards
  override def canBeReverted: Boolean = false

  override def getLogEvent(gameState: GameState): Option[LogEvent] = Some(LogEvent.Scry(player, cardsOnTop.length, cardsOnBottom.length))
}
