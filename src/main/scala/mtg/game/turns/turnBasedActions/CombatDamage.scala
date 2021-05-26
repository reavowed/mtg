package mtg.game.turns.turnBasedActions

import mtg.events.DealDamage
import mtg.game.state._
import mtg.game.state.history.LogEvent

object CombatDamage extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val attackers = DeclareAttackers.getAttackingCreatures(currentGameState)
    if (attackers.nonEmpty)
      (Seq(DealCombatDamageAll(attackers)), None)
    else
      (Nil, None)
  }
}

case class DealCombatDamageAll(attackers: Seq[AttackingCreatureDetails]) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    attackers.map(DealCombatDamage)
  }
}
case class DealCombatDamage(attacker: AttackingCreatureDetails) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    DealDamage(attacker.attacker, attacker.attackedPlayer, attacker.attacker.currentCharacteristics(currentGameState).power.getOrElse(0))
  }
}
