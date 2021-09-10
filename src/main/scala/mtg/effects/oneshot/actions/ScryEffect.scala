package mtg.effects.oneshot.actions

import mtg.effects.{EffectContext, OneShotEffect, StackObjectResolutionContext}
import mtg.effects.oneshot.{OneShotEffectChoice, OneShotEffectResult}
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameActionResult, GameObjectAction, GameObjectActionResult, GameState}
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

case class ScryDecision(cardsOnTop: Seq[ObjectId], cardsOnBottom: Seq[ObjectId])
case class ScryChoice(
    playerChoosing: PlayerId,
    cardsBeingScryed: Seq[ObjectId],
    resolutionContext: StackObjectResolutionContext)
  extends OneShotEffectChoice
{
  override def handleDecision(serializedDecision: String, currentGameState: GameState): Option[(AnyRef, GameActionResult, StackObjectResolutionContext)] = {
    for {
      (serializedCardsOnTop, serializedCardsOnBottom) <- serializedDecision.split("\\|", -1).toSeq match {
        case Seq(a, b) => Some(a, b)
        case _ => None
      }
      cardsOnTop <- ParsingUtils.splitStringAsIds(serializedCardsOnTop)
      cardsOnBottom <- ParsingUtils.splitStringAsIds(serializedCardsOnBottom)
      if (cardsOnTop ++ cardsOnBottom).sortBy(_.sequentialId) == cardsBeingScryed.sortBy(_.sequentialId)
    } yield (
      ScryDecision(cardsOnTop, cardsOnBottom),
      (ScryAction(playerChoosing, cardsOnTop, cardsOnBottom), LogEvent.Scry(playerChoosing, cardsOnTop.length, cardsOnBottom.length)),
      resolutionContext
    )
  }

  override def temporarilyVisibleObjects: Seq[ObjectId] = cardsBeingScryed
}

case class ScryAction(
  player: PlayerId,
  cardsOnTop: Seq[ObjectId],
  cardsOnBottom: Seq[ObjectId]
) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    Zone.Library(player).updateState(currentGameState.gameObjectState, library => {
      val onTop = cardsOnTop.map(id => library.find(_.objectId == id).get)
      val onBottom = cardsOnBottom.map(id => library.find(_.objectId == id).get)
      onTop ++ library.filter(c => !(cardsOnTop ++ cardsOnBottom).contains(c.objectId)) ++ onBottom
    })
  }
}
