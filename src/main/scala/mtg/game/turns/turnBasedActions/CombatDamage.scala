package mtg.game.turns.turnBasedActions

import mtg.game.PlayerIdentifier
import mtg.game.objects.ObjectId
import mtg.game.state._
import mtg.game.state.history.LogEvent
import mtg.parts.damage.{DamageRecipient, DealDamageEvent}
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

case class AssignAttackerCombatDamage(attackDeclarations: Seq[AttackDeclaration], blockDeclarations: Seq[BlockDeclaration], damageEvents: Seq[DealCombatDamageEvent]) extends InternalGameAction {
  private def requiredDamageForLethal(blocker: ObjectId, gameState: GameState): Int = {
    blocker.getToughness(gameState) - blocker.getMarkedDamage(gameState) - damageEvents.filter(_.recipient == DamageRecipient.Creature(blocker)).map(_.amount).sum
  }

  override def execute(currentGameState: GameState): InternalGameActionResult = {
    attackDeclarations match {
      case attackDeclaration +: remainingAttackDeclarations =>
        import attackDeclaration._
        val power = attacker.getPower(currentGameState)
        DeclareBlockers.getBlockerOrdering(attacker, currentGameState) match {
          case Some(blockers) =>
            blockers match {
              case Nil =>
                AssignAttackerCombatDamage(remainingAttackDeclarations, blockDeclarations, damageEvents)
              case Seq(blocker) =>
                AssignAttackerCombatDamage(
                  remainingAttackDeclarations,
                  blockDeclarations,
                  damageEvents :+ DealCombatDamageEvent(attacker, DamageRecipient.Creature(blocker), power))
              case blocker +: _ if requiredDamageForLethal(blocker, currentGameState) >= power =>
                AssignAttackerCombatDamage(
                  remainingAttackDeclarations,
                  blockDeclarations,
                  damageEvents :+ DealCombatDamageEvent(attacker, DamageRecipient.Creature(blocker), power))
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
              damageEvents :+ DealCombatDamageEvent(attacker, DamageRecipient.Player(attackedPlayer), power))
        }
      case Nil =>
        val blockerDamageEvents = blockDeclarations.map { blockDeclaration =>
          DealCombatDamageEvent(blockDeclaration.blocker, DamageRecipient.Creature(blockDeclaration.attacker), blockDeclaration.blocker.getPower(currentGameState))
        }
        damageEvents ++ blockerDamageEvents
    }
  }
}

case class CombatDamageAssignment(blockerDamage: Map[ObjectId, Int])

case class AssignCombatDamageChoice(
    playerToAct: PlayerIdentifier,
    attacker: ObjectId,
    blockers: Seq[(ObjectId, Int)],
    damageToAssign: Int,
    attackedPlayer: PlayerIdentifier,
    attackDeclarations: Seq[AttackDeclaration],
    blockDeclarations: Seq[BlockDeclaration],
    damageEvents: Seq[DealCombatDamageEvent])
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

  override def handleDecision(chosenOption: CombatDamageAssignment, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val assignedDamageEvents = chosenOption.blockerDamage.map { case (blocker, amount) => DealCombatDamageEvent(attacker, DamageRecipient.Creature(blocker), amount)}.toSeq
    (Seq(AssignAttackerCombatDamage(attackDeclarations, blockDeclarations, damageEvents ++ assignedDamageEvents)), None)
  }
}

case class DealCombatDamageEvent(source: ObjectId, recipient: DamageRecipient, amount: Int) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    Seq(DealDamageEvent(source, recipient, amount))
  }
}
