package mtg.game.turns.turnBasedActions

import mtg.game.state._
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}
import mtg.parts.damage.DealDamageEvent
import mtg.utils.ParsingUtils

import scala.annotation.tailrec

object CombatDamage extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    val attackDeclarations = DeclareAttackers.getAttackDeclarations(gameState)
    val blockDeclarations = DeclareBlockers.getBlockDeclarations(gameState)
    if (attackDeclarations.nonEmpty)
      AssignAttackerCombatDamage(attackDeclarations, blockDeclarations, Nil)
    else
      ()
  }
  override def canBeReverted: Boolean = true
}

case class AssignAttackerCombatDamage(attackDeclarations: Seq[AttackDeclaration], blockDeclarations: Seq[BlockDeclaration], damageEvents: Seq[DealCombatDamageEvent]) extends InternalGameAction {
  private def requiredDamageForLethal(blocker: ObjectId, gameState: GameState): Int = {
    blocker.getToughness(gameState) - blocker.getMarkedDamage(gameState) - damageEvents.filter(_.recipient == blocker).map(_.amount).sum
  }

  override def execute(gameState: GameState): GameActionResult = {
    attackDeclarations match {
      case attackDeclaration +: remainingAttackDeclarations =>
        import attackDeclaration._
        val power = attacker.getPower(gameState)
        DeclareBlockers.getOrderingOfBlockersForAttacker(attacker, gameState) match {
          case Some(blockers) =>
            blockers match {
              case Nil =>
                AssignAttackerCombatDamage(remainingAttackDeclarations, blockDeclarations, damageEvents)
              case Seq(blocker) =>
                AssignAttackerCombatDamage(
                  remainingAttackDeclarations,
                  blockDeclarations,
                  damageEvents :+ DealCombatDamageEvent(attacker, blocker, power))
              case blocker +: _ if requiredDamageForLethal(blocker, gameState) >= power =>
                AssignAttackerCombatDamage(
                  remainingAttackDeclarations,
                  blockDeclarations,
                  damageEvents :+ DealCombatDamageEvent(attacker, blocker, power))
              case blockers =>
                AssignCombatDamageChoice(
                  gameState.activePlayer,
                  attacker,
                  blockers.map(b => (b, requiredDamageForLethal(b, gameState))),
                  attacker.getPower(gameState),
                  attackedPlayer,
                  remainingAttackDeclarations,
                  blockDeclarations,
                  damageEvents)
            }
          case None =>
            AssignAttackerCombatDamage(
              remainingAttackDeclarations,
              blockDeclarations,
              damageEvents :+ DealCombatDamageEvent(attacker, attackedPlayer, power))
        }
      case Nil =>
        val blockerDamageEvents = blockDeclarations.map { blockDeclaration =>
          DealCombatDamageEvent(blockDeclaration.blocker, blockDeclaration.attacker, blockDeclaration.blocker.getPower(gameState))
        }
        damageEvents ++ blockerDamageEvents
    }
  }
  override def canBeReverted: Boolean = true
}

case class AssignCombatDamageChoice(
    playerToAct: PlayerId,
    attacker: ObjectId,
    blockers: Seq[(ObjectId, Int)],
    damageToAssign: Int,
    attackedPlayer: PlayerId,
    attackDeclarations: Seq[AttackDeclaration],
    blockDeclarations: Seq[BlockDeclaration],
    damageEvents: Seq[DealCombatDamageEvent])
  extends Choice
{
  override def parseDecision(serializedChosenOption: String): Option[Decision] = {
    @tailrec
    def matchBlockers(remainingIdsAndDamageAmounts: Seq[Int], unmatchedBlockers: Seq[(ObjectId, Int)], assignedBlockerDamage: Map[ObjectId, Int], remainingDamage: Int): Option[Map[ObjectId, Int]] = {
      unmatchedBlockers match {
        case (blocker, requiredDamage) +: otherBlockers =>
          remainingIdsAndDamageAmounts match {
            case blocker.sequentialId +: assignedDamage +: otherInts
              if assignedDamage <= remainingDamage && (assignedDamage >= requiredDamage || assignedDamage == remainingDamage)
            =>
              matchBlockers(otherInts, otherBlockers, assignedBlockerDamage + (blocker -> assignedDamage), remainingDamage - assignedDamage)
            case _ =>
              None
          }
        case Nil =>
          remainingIdsAndDamageAmounts match {
            case Nil =>
              Some(assignedBlockerDamage)
            case _ =>
              None
          }
      }
    }
    for {
      idsAndDamageAmounts <- ParsingUtils.splitStringAsInts(serializedChosenOption)
      damageAssignment <- matchBlockers(idsAndDamageAmounts, blockers, Map.empty, damageToAssign)
      assignedDamageEvents = damageAssignment.map { case (blocker, amount) => DealCombatDamageEvent(attacker, blocker, amount)}.toSeq
    } yield AssignAttackerCombatDamage(attackDeclarations, blockDeclarations, damageEvents ++ assignedDamageEvents)
  }
}

case class DealCombatDamageEvent(source: ObjectId, recipient: ObjectOrPlayer, amount: Int) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    Seq(DealDamageEvent(source, recipient, amount))
  }
  override def canBeReverted: Boolean = true
}
