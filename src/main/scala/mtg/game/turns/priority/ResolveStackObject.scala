package mtg.game.turns.priority

import mtg.characteristics.types.Type
import mtg.events.MoveObjectEvent
import mtg.game.Zone
import mtg.game.objects.GameObject
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction}

case class ResolveStackObject(stackObject: GameObject) extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val stackObjectWithState = currentGameState.derivedState.objectStates(stackObject.objectId)
    if (stackObjectWithState.characteristics.types.exists(_.isInstanceOf[Type.PermanentType])) {
      // RULE 608.3 / Apr 22 2021 : If the object that's resolving is a permanent spell, its resolution involves a single
      // step (unless it's an Aura, a copy of a permanent spell, or a mutating creature spell). The spell card becomes a
      // permanent and is put onto the battlefield under the control of the spell's controller.
      // TODO: Handle the exceptions above
      val controller = stackObjectWithState.controller.get
      (
        Seq(MoveObjectEvent(controller, stackObject, Zone.Battlefield)),
        Some(LogEvent.ResolvePermanent(controller, stackObjectWithState.characteristics.name.getOrElse("<unnamed permanent>")))
      )
    } else ???
  }
}
