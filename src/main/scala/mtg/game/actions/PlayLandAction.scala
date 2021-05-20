package mtg.game.actions

import mtg.characteristics.types.Type
import mtg.game.{PlayerIdentifier, Zone}
import mtg.game.objects.CardObject
import mtg.game.state.history.{GameEvent, LogEvent}
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
  private def getNumberOfLandsPlayedThisTurn(gameState: GameState): Int = {
    gameState.gameHistory.forCurrentTurn.toSeq.flatMap(_.gameEvents)
      .ofType[GameEvent.ResolvedEvent].map(_.event)
      .count(_.isInstanceOf[PlayLandEvent])
  }
  private def getNumberOfLandPlaysAvailable(gameState: GameState): Int = {
    // TODO: effects such as Explore / Azusa
    1
  }
  private def cannotPlayLands(player: PlayerIdentifier, gameState: GameState): Boolean = {
    // TODO: effects such as Aggressive Mining
    if (player != gameState.activePlayer) {
      // RULE: 305.3 / Apr 22 2021 :  A player can’t play a land, for any reason, if it isn’t their turn. Ignore any
      // part of an effect that instructs a player to do so.
      true
    } else if (getNumberOfLandPlaysAvailable(gameState) <= getNumberOfLandsPlayedThisTurn(gameState)) {
      // RULE: 305.2b / Apr 22 2021 : A player can’t play a land, for any reason, if the number of lands the player can
      // play this turn is equal to or less than the number of lands they have already played this turn. Ignore any part
      // of an effect that instructs a player to do so.
      true
    } else {
      false
    }
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
