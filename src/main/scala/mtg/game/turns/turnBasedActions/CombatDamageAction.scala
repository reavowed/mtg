package mtg.game.turns.turnBasedActions

import mtg.actions.damage.DealDamageAction
import mtg.core.{ObjectId, ObjectOrPlayerId, PlayerId}
import mtg.game.state._
import mtg.utils.ParsingUtils

import scala.annotation.tailrec

object CombatDamageAction extends DelegatingGameAction[Unit] {
  override def delegate(implicit gameState: GameState): GameAction[Unit] = {
    val attackers = DeclareAttackers.getAttackers(gameState)
    if (attackers.nonEmpty)
      for {
        attackerDamageEvents <- attackers.foldLeft[GameAction[Seq[DealCombatDamageEvent]]](Nil) { case (eventsAction, attacker) =>
          eventsAction.flatMap(assignAttackerCombatDamage(attacker, _))
        }
        blockerDamageEvents = for {
          blocker <- DeclareBlockers.getBlockers(gameState)
          attacker <- DeclareBlockers.getAttackerForBlocker(blocker, gameState).toSeq
        } yield DealCombatDamageEvent(blocker, attacker, CurrentCharacteristics.getPower(blocker, gameState))
        _ <- WrappedOldUpdates(attackerDamageEvents ++ blockerDamageEvents: _*)
      } yield ()
    else
      ()
  }

  private def assignAttackerCombatDamage(attacker: ObjectId, damageEventsSoFar: Seq[DealCombatDamageEvent])(implicit gameState: GameState): GameAction[Seq[DealCombatDamageEvent]] = {
    val power = CurrentCharacteristics.getPower(attacker, gameState)
    DeclareBlockers.getOrderingOfBlockersForAttacker(attacker, gameState) match {
      case Some(blockers) =>
        blockers match {
          case Nil =>
            Nil
          case Seq(blocker) =>
            Seq(DealCombatDamageEvent(attacker, blocker, power))
          case blocker +: _ if requiredDamageForLethal(blocker, damageEventsSoFar) >= power =>
            Seq(DealCombatDamageEvent(attacker, blocker, power))
          case blockers =>
            AssignCombatDamageChoice(
              gameState.activePlayer,
              attacker,
              blockers.map(b => (b, requiredDamageForLethal(b, damageEventsSoFar))),
              CurrentCharacteristics.getPower(attacker, gameState))
        }
      case None =>
        Seq(DealCombatDamageEvent(attacker, DeclareAttackers.getAttackedPlayer(attacker, gameState), power))
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
  extends Choice[Seq[DealCombatDamageEvent]]
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
    Seq(DealDamageAction(source, recipient, amount))
  }
  override def canBeReverted: Boolean = true
}
