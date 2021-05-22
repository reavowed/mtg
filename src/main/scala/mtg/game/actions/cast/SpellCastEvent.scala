package mtg.game.actions.cast

import mtg.game.objects.GameObject
import mtg.game.state.{GameObjectEvent, GameObjectEventResult, GameState}

case class SpellCastEvent(spell: GameObject) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    // No actual effect, just marking the spell as cast
    ()
  }
}
