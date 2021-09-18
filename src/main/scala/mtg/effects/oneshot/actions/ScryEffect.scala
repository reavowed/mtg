package mtg.effects.oneshot.actions

import mtg.effects.oneshot.{OneShotEffectChoice, OneShotEffectResult}
import mtg.effects.{OneShotEffect, StackObjectResolutionContext}
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameState, InternalGameAction}
import mtg.game.{ObjectId, PlayerId, Zone}
import mtg.utils.ParsingUtils

case class ScryEffect(number: Int) extends OneShotEffect {
  override def getText(cardName: String): String = s"scry $number"
  override def resolve(gameState: GameState, resolutionContext: StackObjectResolutionContext): OneShotEffectResult = {
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
  extends OneShotEffectChoice
{
  override def parseDecision(serializedDecision: String): Option[(Option[InternalGameAction], StackObjectResolutionContext)] = {
    for {
      (serializedCardsOnTop, serializedCardsOnBottom) <- serializedDecision.split("\\|", -1).toSeq match {
        case Seq(a, b) => Some(a, b)
        case _ => None
      }
      cardsOnTop <- ParsingUtils.splitStringAsIds(serializedCardsOnTop)
      cardsOnBottom <- ParsingUtils.splitStringAsIds(serializedCardsOnBottom)
      if (cardsOnTop ++ cardsOnBottom).sortBy(_.sequentialId) == cardsBeingScryed.sortBy(_.sequentialId)
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
      Zone.Library(player).updateState(gameState.gameObjectState, library => {
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