package mtg.game.turns.turnBasedActions

import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.game.state._
import mtg.parts.damage.DealDamageEvent
import mtg.utils.ParsingUtils

import scala.annotation.tailrec

object CombatDamageAction extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val attackDeclarations = DeclareAttackers.getAttackDeclarations(gameState)
    val blockDeclarations = DeclareBlockers.getBlockDeclarations(gameState)
    if (attackDeclarations.nonEmpty)
      assignAttackerCombatDamage(attackDeclarations, blockDeclarations, Nil)
    else
      ()
  }

  private def assignAttackerCombatDamage(
    attackDeclarations: Seq[AttackDeclaration],
    blockDeclarations: Seq[BlockDeclaration],
    damageEvents: Seq[DealCombatDamageEvent])(
    implicit gameState: GameState
  ): PartialGameActionResult[Unit] = {
    attackDeclarations match {
      case attackDeclaration +: remainingAttackDeclarations =>
        import attackDeclaration._
        val power = CurrentCharacteristics.getPower(attacker, gameState)
        DeclareBlockers.getOrderingOfBlockersForAttacker(attacker, gameState) match {
          case Some(blockers) =>
            blockers match {
              case Nil =>
                assignAttackerCombatDamage(remainingAttackDeclarations, blockDeclarations, damageEvents)
              case Seq(blocker) =>
                assignAttackerCombatDamage(
                  remainingAttackDeclarations,
                  blockDeclarations,
                  damageEvents :+ DealCombatDamageEvent(attacker, blocker, power))
              case blocker +: _ if requiredDamageForLethal(blocker, damageEvents) >= power =>
                assignAttackerCombatDamage(
                  remainingAttackDeclarations,
                  blockDeclarations,
                  damageEvents :+ DealCombatDamageEvent(attacker, blocker, power))
              case blockers =>
                PartialGameActionResult.ChildWithCallback[Unit, Seq[DealCombatDamageEvent]](
                  AssignCombatDamageChoice(
                    gameState.activePlayer,
                    attacker,
                    blockers.map(b => (b, requiredDamageForLethal(b, damageEvents))),
                    CurrentCharacteristics.getPower(attacker, gameState)),
                  (newDamageAssignments, gameState) => assignAttackerCombatDamage(remainingAttackDeclarations, blockDeclarations, damageEvents ++ newDamageAssignments)(gameState))
            }
          case None =>
            assignAttackerCombatDamage(
              remainingAttackDeclarations,
              blockDeclarations,
              damageEvents :+ DealCombatDamageEvent(attacker, attackedPlayer, power))
        }
      case Nil =>
        val blockerDamageEvents = blockDeclarations.map { blockDeclaration =>
          DealCombatDamageEvent(blockDeclaration.blocker, blockDeclaration.attacker, CurrentCharacteristics.getPower(blockDeclaration.blocker, gameState))
        }
        PartialGameActionResult.childThenValue(
          WrappedOldUpdates(damageEvents ++ blockerDamageEvents: _*),
          ())
    }
  }

  private def requiredDamageForLethal(
    blocker: ObjectId,
    damageEvents: Seq[DealCombatDamageEvent])(
    implicit gameState: GameState
  ): Int = {
    val blockerToughness = CurrentCharacteristics.getToughness(blocker, gameState)
    val blockerDamage = CurrentCharacteristics.getMarkedDamage(blocker, gameState)
    val totalDamageAssignedSoFar = damageEvents.filter(_.recipient == blocker).map(_.amount).sum
    blockerToughness - blockerDamage - totalDamageAssignedSoFar
  }
}

case class AssignCombatDamageChoice(
    playerToAct: PlayerId,
    attacker: ObjectId,
    blockers: Seq[(ObjectId, Int)],
    damageToAssign: Int)
  extends DirectChoice[Seq[DealCombatDamageEvent]]
{
  object DamageAmount {
    def unapply(text: String): Option[Int] = text.toIntOption
  }

  override def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[Seq[DealCombatDamageEvent]] = {
    @tailrec
    def matchBlockers(remainingIdsAndDamageAmounts: Seq[String], unmatchedBlockers: Seq[(ObjectId, Int)], assignedBlockerDamage: Map[ObjectId, Int], remainingDamage: Int): Option[Map[ObjectId, Int]] = {
      unmatchedBlockers match {
        case (blocker, requiredDamage) +: otherBlockers =>
          remainingIdsAndDamageAmounts match {
            case blocker.toString +: DamageAmount(assignedDamage) +: otherInts
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
    val idsAndDamageAmounts = ParsingUtils.splitStringBySpaces(serializedDecision)
    for {
      damageAssignment <- matchBlockers(idsAndDamageAmounts, blockers, Map.empty, damageToAssign)
      assignedDamageEvents = damageAssignment.map { case (blocker, amount) => DealCombatDamageEvent(attacker, blocker, amount)}.toSeq
    } yield assignedDamageEvents
  }
}

case class DealCombatDamageEvent(source: ObjectId, recipient: ObjectOrPlayerId, amount: Int) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    Seq(DealDamageEvent(source, recipient, amount))
  }
  override def canBeReverted: Boolean = true
}
