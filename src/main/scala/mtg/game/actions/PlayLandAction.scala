package mtg.game.actions

import mtg.characteristics.types.Type
import mtg.game.{PlayerIdentifier, Zone}
import mtg.game.objects.CardObject
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}

case class PlayLandAction(player: PlayerIdentifier, land: CardObject) extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val preventEvent = PlayLandAction.cannotPlayLands(player, currentGameState) || PlayLandAction.cannotPlayLand(land, player, currentGameState)
    val eventOption = if (preventEvent) None else Some(PlayLandEvent(land))
    (
      eventOption.toSeq,
      Some(LogEvent.PlayedLand(player, land.card.printing.cardDefinition.name))
    )
  }
}

object PlayLandAction {
  def getPlayableLands(player: PlayerIdentifier, gameState: GameState): Seq[CardObject] = {
    if (cannotPlayLands(player, gameState) || !canPlayLandsAsSpecialAction(player, gameState))
      Nil
    else
      gameState.gameObjectState.allVisibleObjects(player)
        .ofType[CardObject]
        .filter(_.card.printing.cardDefinition.types.contains(Type.Land))
        .filter(!cannotPlayLand(_, player, gameState))
        .filter(canPlayLandAsSpecialAction(_, player, gameState))
  }
  private def cannotPlayLands(player: PlayerIdentifier, gameState: GameState): Boolean = {
    // TODO: effects such as Aggressive Mining
    player != gameState.activePlayer
  }
  private def cannotPlayLand(land: CardObject, player: PlayerIdentifier, gameState: GameState): Boolean = {
    // TODO: effects such as Experimental Frenzy / Tomik, Distinguished Advokist
    false
  }
  private def canPlayLandsAsSpecialAction(player: PlayerIdentifier, gameState: GameState): Boolean = {
    // RULE: 116.2a / Apr 22 2021 : A player can take this action any time they have priority and the stack is empty
    // during a main phase of their turn.
    // NOTE: Priority check is implicitly implemented by the fact that we only even check this when the player has
    // priority
    // TODO: check timing
    // TODO: check land plays this turn
    true
  }
  private def canPlayLandAsSpecialAction(land: CardObject, player: PlayerIdentifier, gameState: GameState): Boolean = {
    // TODO: effects such as Crucible of Worlds
    land.zone == Zone.Hand(player)
  }
}
