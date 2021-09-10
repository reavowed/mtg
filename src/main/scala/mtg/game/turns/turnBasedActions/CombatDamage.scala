package mtg.game.turns.turnBasedActions

import mtg.game.state._
import mtg.game.state.history.LogEvent
import mtg.game.{ObjectId, ObjectOrPlayer, PlayerId}
import mtg.parts.damage.DealDamageAction
import mtg.utils.ParsingUtils

import scala.annotation.tailrec

object CombatDamage extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val attackDeclarations = DeclareAttackers.getAttackDeclarations(currentGameState)
    val blockDeclarations = DeclareBlockers.getBlockDeclarations(currentGameState)
    if (attackDeclarations.nonEmpty)
      AssignAttackerCombatDamage(attackDeclarations, blockDeclarations, Nil)
    else
      ()
  }
}

case class AssignAttackerCombatDamage(attackDeclarations: Seq[AttackDeclaration], blockDeclarations: Seq[BlockDeclaration], damageEvents: Seq[DealCombatDamageAction]) extends InternalGameAction {
  private def requiredDamageForLethal(blocker: ObjectId, gameState: GameState): Int = {
    blocker.getToughness(gameState) - blocker.getMarkedDamage(gameState) - damageEvents.filter(_.recipient == blocker).map(_.amount).sum
  }

  override def execute(currentGameState: GameState): InternalGameActionResult = {
    attackDeclarations match {
      case attackDeclaration +: remainingAttackDeclarations =>
        import attackDeclaration._
        val power = attacker.getPower(currentGameState)
        DeclareBlockers.getOrderingOfBlockersForAttacker(attacker, currentGameState) match {
          case Some(blockers) =>
            blockers match {
              case Nil =>
                AssignAttackerCombatDamage(remainingAttackDeclarations, blockDeclarations, damageEvents)
              case Seq(blocker) =>
                AssignAttackerCombatDamage(
                  remainingAttackDeclarations,
                  blockDeclarations,
                  damageEvents :+ DealCombatDamageAction(attacker, blocker, power))
              case blocker +: _ if requiredDamageForLethal(blocker, currentGameState) >= power =>
                AssignAttackerCombatDamage(
                  remainingAttackDeclarations,
                  blockDeclarations,
                  damageEvents :+ DealCombatDamageAction(attacker, blocker, power))
              case blockers =>
                AssignCombatDamageChoice(
                  currentGameState.activePlayer,
                  attacker,
                  blockers.map(b => (b, requiredDamageForLethal(b, currentGameState))),
                  attacker.getPower(currentGameState),
                  attackedPlayer,
                  remainingAttackDeclarations,
                  blockDeclarations,
                  damageEvents)
            }
          case None =>
            AssignAttackerCombatDamage(
              remainingAttackDeclarations,
              blockDeclarations,
              damageEvents :+ DealCombatDamageAction(attacker, attackedPlayer, power))
        }
      case Nil =>
        val blockerDamageEvents = blockDeclarations.map { blockDeclaration =>
          DealCombatDamageAction(blockDeclaration.blocker, blockDeclaration.attacker, blockDeclaration.blocker.getPower(currentGameState))
        }
        damageEvents ++ blockerDamageEvents
    }
  }
}

case class CombatDamageAssignment(blockerDamage: Map[ObjectId, Int])

case class AssignCombatDamageChoice(
    playerToAct: PlayerId,
    attacker: ObjectId,
    blockers: Seq[(ObjectId, Int)],
    damageToAssign: Int,
    attackedPlayer: PlayerId,
    attackDeclarations: Seq[AttackDeclaration],
    blockDeclarations: Seq[BlockDeclaration],
    damageEvents: Seq[DealCombatDamageAction])
  extends TypedPlayerChoice[CombatDamageAssignment]
{
  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[CombatDamageAssignment] = {
    @tailrec
    def matchBlockers(remainingInts: Seq[Int], unmatchedBlockers: Seq[(ObjectId, Int)], assignedBlockerDamage: Map[ObjectId, Int], remainingDamage: Int): Option[CombatDamageAssignment] = {
      unmatchedBlockers match {
        case (blocker, requiredDamage) +: otherBlockers =>
          remainingInts match {
            case blocker.sequentialId +: assignedDamage +: otherInts
              if assignedDamage <= remainingDamage && (assignedDamage >= requiredDamage || assignedDamage == remainingDamage)
            =>
              matchBlockers(otherInts, otherBlockers, assignedBlockerDamage + (blocker -> assignedDamage), remainingDamage - assignedDamage)
            case _ =>
              None
          }
        case Nil =>
          remainingInts match {
            case Nil =>
              Some(CombatDamageAssignment(assignedBlockerDamage))
            case _ =>
              None
          }
      }
    }
    ParsingUtils.splitStringAsInts(serializedChosenOption).flatMap(matchBlockers(_, blockers, Map.empty, damageToAssign))
  }

  override def handleDecision(chosenOption: CombatDamageAssignment, currentGameState: GameState): InternalGameActionResult = {
    val assignedDamageEvents = chosenOption.blockerDamage.map { case (blocker, amount) => DealCombatDamageAction(attacker, blocker, amount)}.toSeq
    AssignAttackerCombatDamage(attackDeclarations, blockDeclarations, damageEvents ++ assignedDamageEvents)
  }
}

case class DealCombatDamageAction(source: ObjectId, recipient: ObjectOrPlayer, amount: Int) extends GameObjectAction {
  override def execute(currentGameState: GameState): GameObjectActionResult = {
    Seq(DealDamageAction(source, recipient, amount))
  }
}
