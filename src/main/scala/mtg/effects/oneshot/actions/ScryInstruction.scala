package mtg.effects.oneshot.actions

import mtg.core.zones.Zone
import mtg.core.{ObjectId, PlayerId}
import mtg.effects.oneshot.{InstructionChoice, InstructionResult}
import mtg.effects.{Instruction, StackObjectResolutionContext}
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.utils.ParsingUtils

case class ScryInstruction(number: Int) extends Instruction {
  override def getText(cardName: String): String = s"scry $number"
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): InstructionResult = {
    val player = resolutionContext.controllingPlayer
    val library = gameState.gameObjectState.libraries(player)
    val cardsBeingScryed = library.take(number).map(_.objectId)
    ScryChoice(player, cardsBeingScryed, resolutionContext)
  }
}
case class ScryChoice(
    playerChoosing: PlayerId,
    cardsBeingScryed: Seq[ObjectId],
    resolutionContext: StackObjectResolutionContext)
  extends InstructionChoice
{
  override def parseDecision(serializedDecision: String): Option[(Option[InternalGameAction], StackObjectResolutionContext)] = {
    for {
      (serializedCardsOnTop, serializedCardsOnBottom) <- serializedDecision.split("\\|", -1).toSeq match {
        case Seq(a, b) => Some(a, b)
        case _ => None
      }
      cardsOnTop <- ParsingUtils.splitStringAsIds(serializedCardsOnTop)
      cardsOnBottom <- ParsingUtils.splitStringAsIds(serializedCardsOnBottom)
      if (cardsOnTop ++ cardsOnBottom).toSet == cardsBeingScryed.toSet
    } yield (
      Some(ScryEvent(playerChoosing, cardsOnTop, cardsOnBottom)),
      resolutionContext
    )
  }

  override def temporarilyVisibleObjects: Seq[ObjectId] = cardsBeingScryed
}

case class ScryEvent(
  player: PlayerId,
  cardsOnTop: Seq[ObjectId],
  cardsOnBottom: Seq[ObjectId]
) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    (
      // TODO: Should create new game objects to hide card identities (if scrying more than one)
      gameState.gameObjectState.updateZone(Zone.Library(player), library => {
        val onTop = cardsOnTop.map(id => library.find(_.objectId == id).get)
        val onBottom = cardsOnBottom.map(id => library.find(_.objectId == id).get)
        onTop ++ library.filter(c => !(cardsOnTop ++ cardsOnBottom).contains(c.objectId)) ++ onBottom
      }),
      LogEvent.Scry(player, cardsOnTop.length, cardsOnBottom.length)
    )
  }
  // TODO: Theoretically possible to revert if scrying player already knew the identity of the scryed cards
  override def canBeReverted: Boolean = false
}
