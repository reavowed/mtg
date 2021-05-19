package mtg.game.actions

import mtg.game.PlayerIdentifier
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
