package mtg.game.turns.turnBasedActions

import mtg.continuousEffects.BlockerRestrictionEffect
import mtg.core.types.Type
import mtg.core.{ObjectId, PlayerId}
import mtg.game.state._
import mtg.game.state.history.LogEvent
import mtg.game.turns.TurnPhase
import mtg.utils.ParsingUtils

import scala.collection.View

object DeclareBlockers extends ExecutableGameAction[Unit] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Unit] = {
    val defendingPlayer = DeclareAttackers.getDefendingPlayer(gameState)
    val attackers = DeclareAttackers.getAttackers(gameState)
    val possibleBlockers = getPossibleBlockers(defendingPlayer, attackers, gameState)
    if (attackers.nonEmpty && possibleBlockers.nonEmpty) {
      PartialGameActionResult.ChildWithCallback(
        DeclareBlockersChoice(
          defendingPlayer,
          attackers,
          possibleBlockers),
        handleBlockers)
    } else
      ()
  }

  private def handleBlockers(declaredBlockers: DeclaredBlockers, gameState: GameState): PartialGameActionResult[Unit] = {
    def getName(objectId: ObjectId): String = gameState.gameObjectState.derivedState.permanentStates(objectId).characteristics.name.get
    val assignments = declaredBlockers.blockDeclarations.groupBy(_.attacker)
      .map { case (id, details) => (getName(id), details.map(_.blocker).map(getName)) }
    PartialGameActionResult.childrenThenValue(
      Seq(OrderBlockers(declaredBlockers.blockDeclarations), LogEvent.DeclareBlockers(declaredBlockers.player, assignments)),
      ()
    )(gameState)
  }

  private def getPossibleBlockers(defendingPlayer: PlayerId, attackers: Seq[ObjectId], gameState: GameState): Map[ObjectId, Seq[ObjectId]] = {
    val attackerStates = attackers.map(gameState.gameObjectState.derivedState.permanentStates)
    val allPossibleBlockers = gameState.gameObjectState.derivedState.permanentStates.values.view
      .filter(o => o.characteristics.types.contains(Type.Creature))
      .filter(o => o.controller == defendingPlayer)
      .filter(o => !o.gameObject.status.isTapped)
      .toSeq
    val restrictions = gameState.gameObjectState.activeContinuousEffects.ofType[BlockerRestrictionEffect].toSeq
    allPossibleBlockers.map(blockerState => {
      blockerState.gameObject.objectId -> attackerStates.filter(attackerState => !restrictions.exists(_.preventsBlock(attackerState, blockerState))).map(_.gameObject.objectId)
    }).filter(_._2.nonEmpty).toMap
  }
  def getBlockDeclarations(gameState: GameState): Seq[BlockDeclaration] = {
    gameState.gameHistory.gameEventsThisTurn.decisions[DeclaredBlockers]
      .toSeq
      .flatMap(_.blockDeclarations)
  }
  def getBlockers(gameState: GameState): Seq[ObjectId] = {
    getBlockDeclarations(gameState)
      .map(_.blocker)
      .filter(DeclareAttackers.isStillInCombat(_, gameState))
  }
  def isBlocking(objectId: ObjectId, gameState: GameState): Boolean = {
    getBlockers(gameState).contains(objectId)
  }

  def getBlockerOrderings(gameState: GameState): Seq[BlockerOrdering] = {
    gameState.gameHistory.gameEventsThisTurn.decisions[BlockerOrdering].toSeq
  }
  def getDeclaredBlockersForAttacker(attacker: ObjectId, gameState: GameState): Set[ObjectId] = {
    getBlockDeclarations(gameState).view
      .filter(_.attacker == attacker)
      .map(_.blocker)
      .toSet
  }
  def getDefaultBlockerOrdering(attacker: ObjectId, gameState: GameState): Option[Seq[ObjectId]] = {
    val blockers = getDeclaredBlockersForAttacker(attacker, gameState)
    if (blockers.size == 1) {
      Some(blockers.toSeq)
    } else {
      None
    }
  }
  def getOrderingOfBlockersForAttacker(attacker: ObjectId, gameState: GameState): Option[Seq[ObjectId]] = {
    getBlockerOrderings(gameState)
      .find(_.attacker == attacker)
      .map(_.blockersInOrder)
      .orElse(getDefaultBlockerOrdering(attacker, gameState))
      .map(_.filter(DeclareAttackers.isStillInCombat(_, gameState)))
  }
  def getAttackerForBlocker(blocker: ObjectId, gameState: GameState): Option[ObjectId] = {
    getBlockDeclarations(gameState)
      .find(_.blocker == blocker)
      .map(_.attacker)
      .filter(DeclareAttackers.isStillInCombat(_, gameState))
  }
}

case class DeclareBlockersChoice(
    playerToAct: PlayerId,
    attackers: Seq[ObjectId],
    possibleBlockers: Map[ObjectId, Seq[ObjectId]])
  extends Choice[DeclaredBlockers]
{
  override def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[DeclaredBlockers] = {
    ParsingUtils.splitStringAsIds(serializedDecision)
      .map(_.grouped(2).toSeq)
      .filter(_.forall(_.length == 2))
      .map(_.map { case Seq(a, b) => BlockDeclaration(a, b)} )
      .filter(_.forall(isValidBlock))
      .map(DeclaredBlockers(playerToAct, _))
  }
  private def isValidBlock(blockDeclaration: BlockDeclaration): Boolean = {
    possibleBlockers.get(blockDeclaration.blocker).exists(_.contains(blockDeclaration.attacker))
  }
}

case class BlockDeclaration(blocker: ObjectId, attacker: ObjectId)
case class DeclaredBlockers(player: PlayerId, blockDeclarations: Seq[BlockDeclaration])

case class OrderBlockers(blockDeclarations: Seq[BlockDeclaration]) extends ExecutableGameAction[Seq[BlockerOrdering]] {
  override def execute()(implicit gameState: GameState): PartialGameActionResult[Seq[BlockerOrdering]] = {
    PartialGameActionResult.children(blockDeclarations
      .groupBy(_.attacker)
      .filter(_._2.length > 1)
      .map { case (attacker, blockDeclarations) =>
        OrderBlockersChoice(gameState.activePlayer, attacker, blockDeclarations.map(_.blocker).toSet)
      }.toSeq: _*)
  }
}

case class OrderBlockersChoice(playerToAct: PlayerId, attacker: ObjectId, blockers: Set[ObjectId]) extends Choice[BlockerOrdering] {
  override def handleDecision(serializedDecision: String)(implicit gameState: GameState): Option[BlockerOrdering] = {
    for {
      blockersInOrder <- ParsingUtils.splitStringAsIds(serializedDecision)
      if blockersInOrder.toSet == blockers
    } yield BlockerOrdering(playerToAct, attacker, blockersInOrder)
  }
}

case class BlockerOrdering(player: PlayerId, attacker: ObjectId, blockersInOrder: Seq[ObjectId]) extends InternalGameAction {
  override def execute(gameState: GameState): GameActionResult = {
    def getName(objectId: ObjectId): String = gameState.gameObjectState.derivedState.permanentStates(objectId).characteristics.name.get
    LogEvent.OrderBlockers(
      player,
      getName(attacker),
      blockersInOrder.map(getName))
  }
  override def canBeReverted: Boolean = false
}
