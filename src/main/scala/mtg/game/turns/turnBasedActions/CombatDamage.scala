package mtg.game.turns.turnBasedActions

import mtg._
import mtg.events.DealDamage
import mtg.game.state.history.GameEvent.Decision
import mtg.game.state.history.LogEvent
import mtg.game.state._

object CombatDamage extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val attackersOption = currentGameState.gameHistory.forCurrentTurn
      .flatMap(
        _.gameEvents.view.ofType[Decision]
          .map(_.chosenOption)
          .mapFind(_.asOptionalInstanceOf[DeclaredAttackers]))
      .map(_.attackers)
    (attackersOption.map(DealCombatDamageAll).toSeq, None)
  }
}

case class DealCombatDamageAll(attackers: Seq[DeclaredAttacker]) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    attackers.map(DealCombatDamage)
  }
}
case class DealCombatDamage(attacker: DeclaredAttacker) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    DealDamage(attacker.attacker, attacker.attackedPlayer, attacker.attacker.currentCharacteristics(currentGameState).power.getOrElse(0))
  }
}
