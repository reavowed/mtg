package mtg.game.turns.turnBasedActions

import mtg._
import mtg.core.types.Type
import mtg.core.{ObjectId, PlayerId}
import mtg.actions.TapObjectAction
import mtg.game.state._
import mtg.game.state.history.HistoryEvent.ResolvedAction
import mtg.game.state.history.LogEvent
import mtg.game.turns.TurnPhase
import mtg.utils.ParsingUtils

object DeclareAttackers extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val possibleAttackers = getPossibleAttackers(gameState)
    if (possibleAttackers.nonEmpty) {
      PartialGameActionResult.ChildWithCallback(
        DeclareAttackersChoice(
          gameState.activePlayer,
          getDefendingPlayer(gameState),
          possibleAttackers),
        handleAttackers)
    } else ()
  }

  private def handleAttackers(declaredAttackers: DeclaredAttackers, gameState: GameState): PartialGameActionResult[Unit] = {
    PartialGameActionResult.childrenThenValue(
      Seq(
        WrappedOldUpdates(declaredAttackers.attackDeclarations.map(_.attacker).map(TapAttacker): _*),
        LogEvent.DeclareAttackers(
          declaredAttackers.player,
          declaredAttackers.attackDeclarations.map(_.attacker).map(CurrentCharacteristics.getName(_, gameState)))),
      ())(gameState)
  }

  private def wasContinuouslyControlled(objectId: ObjectId, gameState: GameState): Boolean = {
    gameState.gameHistory.gameEventsThisTurn.ofType[ResolvedAction]
      .forall(_.stateBefore.gameObjectState.derivedState.permanentStates.get(objectId).exists(_.controller == gameState.activePlayer))
  }
  private def getPossibleAttackers(gameState: GameState): Seq[ObjectId] = {
    gameState.gameObjectState.derivedState.permanentStates.values.view
      .filter(o => o.characteristics.types.contains(Type.Creature))
      .filter(o => o.controller == gameState.activePlayer)
      .filter(o => !o.gameObject.status.isTapped)
      .filter(o => wasContinuouslyControlled(o.gameObject.objectId, gameState))
      .map(o => o.gameObject.objectId)
      .toSeq
  }

  def getDefendingPlayer(gameState: GameState): PlayerId = {
    gameState.playersInApnapOrder.filter(_ != gameState.activePlayer).single
  }
  def getAttackDeclarations(gameState: GameState): Seq[AttackDeclaration] = {
    gameState.gameHistory.gameEventsThisTurn.decisions[DeclaredAttackers]
      .toSeq
      .flatMap(_.attackDeclarations)
  }

  def isAttacking(objectId: ObjectId, gameState: GameState): Boolean = {
    def wasDeclaredAttacker = getAttackDeclarations(gameState).exists(_.attacker == objectId)
    wasDeclaredAttacker && gameState.currentPhase.contains(TurnPhase.CombatPhase)
  }
}

case class AttackDeclaration(attacker: ObjectId, attackedPlayer: PlayerId)
case class DeclaredAttackers(player: PlayerId, attackDeclarations: Seq[AttackDeclaration])

case class DeclareAttackersChoice(playerToAct: PlayerId, defendingPlayer: PlayerId, possibleAttackers: Seq[ObjectId]) extends Choice[DeclaredAttackers] {
  override def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[DeclaredAttackers] = {
    ParsingUtils.splitStringBySpaces(serializedDecision)
      .map(id => possibleAttackers.find(_.toString == id).map(AttackDeclaration(_, defendingPlayer)))
      .swap
      .map(DeclaredAttackers(playerToAct, _))
  }
}

case class TapAttacker(attacker: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    TapObjectAction(attacker)
  }
  override def canBeReverted: Boolean = true
}
