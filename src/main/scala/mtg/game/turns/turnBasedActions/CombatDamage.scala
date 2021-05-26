package mtg.game.turns.turnBasedActions

import mtg.game.objects.ObjectId
import mtg.game.state._
import mtg.game.state.history.LogEvent
import mtg.parts.damage.{DamageRecipient, DealDamageEvent}

object CombatDamage extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val attackDeclarations = DeclareAttackers.getAttackDeclarations(currentGameState)
    val blockDeclarations = DeclareBlockers.getBlockDeclarations(currentGameState)
    if (attackDeclarations.nonEmpty)
      (Seq(DealAllCombatDamage(attackDeclarations, blockDeclarations)), None)
    else
      (Nil, None)
  }
}

case class DealAllCombatDamage(attackDeclarations: Seq[AttackDeclaration], blockDeclarations: Seq[BlockDeclaration]) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    val attackerDamage = attackDeclarations.map(attackDeclaration => {
      import attackDeclaration._
      val damageToDeal = attacker.currentCharacteristics(currentGameState).power.getOrElse(0)
      val blockers = DeclareBlockers.getBlockDeclarationsForAttacker(attacker, blockDeclarations).map(_.blocker)
      if (blockers.isEmpty) {
        DealCombatDamageEvent(attacker, DamageRecipient.Player(attackedPlayer), damageToDeal)
      } else {
        DealCombatDamageEvent(attacker, DamageRecipient.Creature(blockers.single), damageToDeal)
      }
    })
    val blockerDamage = blockDeclarations.map(blockDeclaration => {
      import blockDeclaration._
      val damageToDeal = blocker.currentCharacteristics(currentGameState).power.getOrElse(0)
      DealCombatDamageEvent(blocker, DamageRecipient.Creature(blockedCreature), damageToDeal)
    })
    attackerDamage ++ blockerDamage
  }
}
case class DealCombatDamageEvent(source: ObjectId, recipient: DamageRecipient, amount: Int) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    Seq(DealDamageEvent(source, recipient, amount))
  }
}
