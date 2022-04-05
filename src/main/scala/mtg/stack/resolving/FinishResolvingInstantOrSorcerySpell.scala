package mtg.stack.resolving

import mtg.actions.moveZone.MoveToGraveyardAction
import mtg.game.state.history.LogEvent
import mtg.game.state.{DelegatingGameAction, GameAction, GameState, StackObjectWithState}

case class FinishResolvingInstantOrSorcerySpell(spell: StackObjectWithState) extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    MoveToGraveyardAction(spell.gameObject.objectId)
      .andThen(LogEvent.ResolveSpell(spell.controller, spell.characteristics.name.get))
  }
}
