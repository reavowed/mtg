package mtg.game.actions

import mtg.characteristics.types.Type
import mtg.game.{PlayerIdentifier, Zone}
import mtg.game.objects.CardObject
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}

case class PlayLandAction(player: PlayerIdentifier, land: CardObject) extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    (
      Seq(PlayLandEvent(land)),
      Some(LogEvent.PlayedLand(player, land.card.printing.cardDefinition.name))
    )
  }
}

object PlayLandAction {
  def getPlayableLands(player: PlayerIdentifier, gameState: GameState): Seq[CardObject] = {
    if (!canPlayLands(player, gameState))
      Nil
    else
      gameState.gameObjectState.allVisibleObjects(player)
        .ofType[CardObject]
        .filter(_.card.printing.cardDefinition.types.contains(Type.Land))
        .filter(canPlayLand(_, player, gameState))
  }
  private def canPlayLands(player: PlayerIdentifier, gameState: GameState): Boolean = {
     player == gameState.activePlayer
  }
  private def canPlayLand(land: CardObject, player: PlayerIdentifier, gameState: GameState): Boolean = {
    land.zone == Zone.Hand(player)
  }
}
