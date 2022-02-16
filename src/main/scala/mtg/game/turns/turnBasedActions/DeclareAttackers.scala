package mtg.game.turns.turnBasedActions

import mtg._
import mtg.characteristics.types.Type
import mtg.events.TapObjectEvent
import mtg.game.state._
import mtg.game.state.history.HistoryEvent.ResolvedAction
import mtg.game.state.history.LogEvent
import mtg.game.turns.TurnPhase
import mtg.game.{ObjectId, PlayerId}
import mtg.utils.ParsingUtils

object DeclareAttackers extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    val possibleAttackers = getPossibleAttackers(gameState)
    if (possibleAttackers.nonEmpty)
      DeclareAttackersChoice(
        gameState.activePlayer,
        getDefendingPlayer(gameState),
        possibleAttackers)
    else
      ()
  }
  override def canBeReverted: Boolean = true

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
    gameState.gameHistory.gameEventsThisTurn.actions.ofType[DeclaredAttackers]
      .toSeq
      .flatMap(_.attackDeclarations)
  }

  def isAttacking(objectId: ObjectId, gameState: GameState): Boolean = {
    def wasDeclaredAttacker = getAttackDeclarations(gameState).exists(_.attacker == objectId)
    wasDeclaredAttacker && gameState.currentPhase.contains(TurnPhase.CombatPhase)
  }
}

case class AttackDeclaration(attacker: ObjectId, attackedPlayer: PlayerId)
case class DeclaredAttackers(player: PlayerId, attackDeclarations: Seq[AttackDeclaration]) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    (
      attackDeclarations.map(_.attacker).map(TapAttacker),
      LogEvent.DeclareAttackers(
        player,
        attackDeclarations.map(_.attacker).map(CurrentCharacteristics.getName(_, gameState))
      )
    )
  }
  override def canBeReverted: Boolean = false
}

case class DeclareAttackersChoice(playerToAct: PlayerId, defendingPlayer: PlayerId, possibleAttackers: Seq[ObjectId]) extends Choice {
  override def parseDecision(serializedChosenOption: String): Option[Decision] = {
    ParsingUtils.splitStringBySpaces(serializedChosenOption)
      .map(id => possibleAttackers.find(_.toString == id).map(AttackDeclaration(_, defendingPlayer)))
      .swap
      .map(DeclaredAttackers(playerToAct, _))
  }
}

case class TapAttacker(attacker: ObjectId) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    TapObjectEvent(attacker)
  }
  override def canBeReverted: Boolean = true
}
