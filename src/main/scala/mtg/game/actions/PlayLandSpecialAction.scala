package mtg.game.actions

import mtg.characteristics.types.Type
import mtg.game.state.history.{GameEvent, LogEvent}
import mtg.game.state.{GameState, GameActionResult, ObjectWithState}
import mtg.game.{ObjectId, PlayerId, Zone}

case class PlayLandSpecialAction(player: PlayerId, land: ObjectWithState) extends PriorityAction {
  override def objectId: ObjectId = land.gameObject.objectId
  override def displayText: String = "Play"
  override def optionText: String = "Play " + land.gameObject.objectId

  override def execute(currentGameState: GameState): GameActionResult = {
    val preventEvent = PlayLandSpecialAction.cannotPlayLands(player, currentGameState) || PlayLandSpecialAction.cannotPlayLand(land, player, currentGameState)
    val eventOption = if (preventEvent) None else Some(PlayLandAction(player, land.gameObject))
    GameActionResult(
      eventOption.toSeq,
      Some(LogEvent.PlayedLand(player, land.characteristics.name.get))
    )
  }
}

object PlayLandSpecialAction {
  def getPlayableLands(player: PlayerId, gameState: GameState): Seq[PlayLandSpecialAction] = {
    if (cannotPlayLands(player, gameState) || !canPlayLandsAsSpecialAction(player, gameState))
      Nil
    else
      gameState.gameObjectState.derivedState.allObjectStates.values.view
        .filter(_.characteristics.types.contains(Type.Land))
        .filter(!cannotPlayLand(_, player, gameState))
        .filter(canPlayLandAsSpecialAction(_, player, gameState))
        .map(PlayLandSpecialAction(player, _))
        .toSeq
  }
  private def getNumberOfLandsPlayedThisTurn(gameState: GameState): Int = {
    gameState.eventsThisTurn
      .ofType[GameEvent.ResolvedEvent].map(_.event)
      .count(_.isInstanceOf[PlayLandAction])
  }
  private def getNumberOfLandPlaysAvailable(gameState: GameState): Int = {
    // TODO: effects such as Explore / Azusa
    1
  }
  private def cannotPlayLands(player: PlayerId, gameState: GameState): Boolean = {
    // TODO: effects such as Aggressive Mining
    if (!TimingChecks.isPlayersTurn(player, gameState)) {
      // RULE: 305.3 / Apr 22 2021 : A player can’t play a land, for any reason, if it isn’t their turn. Ignore any
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
  private def cannotPlayLand(land: ObjectWithState, player: PlayerId, gameState: GameState): Boolean = {
    // TODO: effects such as Experimental Frenzy / Tomik, Distinguished Advokist
    false
  }
  private def canPlayLandsAsSpecialAction(player: PlayerId, gameState: GameState): Boolean = {
    // RULE: 116.2a / Apr 22 2021 : A player can take this action any time they have priority and the stack is empty
    // during a main phase of their turn.
    // NOTE: Check for whose turn it is controlled by cannotPlayLands method above
    // NOTE: Check for priority is implicitly implemented by the fact that we only even check this when the player has
    // priority
    TimingChecks.isMainPhaseOfPlayersTurnWithEmptyStack(player, gameState)
  }
  private def canPlayLandAsSpecialAction(land: ObjectWithState, player: PlayerId, gameState: GameState): Boolean = {
    // TODO: effects such as Crucible of Worlds
    land.gameObject.zone == Zone.Hand(player)
  }
}
