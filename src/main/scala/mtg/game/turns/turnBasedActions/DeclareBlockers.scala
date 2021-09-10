package mtg.game.turns.turnBasedActions

import mtg.characteristics.types.Type
import mtg.effects.continuous.BlockerRestriction
import mtg.game.{ObjectId, PlayerId}
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, InternalGameActionResult, GameState, InternalGameAction, TypedPlayerChoice}
import mtg.game.turns.TurnPhase
import mtg.utils.ParsingUtils

object DeclareBlockers extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val defendingPlayer = DeclareAttackers.getDefendingPlayer(currentGameState)
    val attackers = DeclareAttackers.getAttackDeclarations(currentGameState).map(_.attacker)
    val possibleBlockers = getPossibleBlockers(defendingPlayer, attackers, currentGameState)
    if (attackers.nonEmpty && possibleBlockers.nonEmpty)
      DeclareBlockersChoice(
        defendingPlayer,
        attackers,
        possibleBlockers)
    else
      ()
  }
  private def getPossibleBlockers(defendingPlayer: PlayerId, attackers: Seq[ObjectId], gameState: GameState): Map[ObjectId, Seq[ObjectId]] = {
    val attackerStates = attackers.map(gameState.gameObjectState.derivedState.permanentStates)
    val allPossibleBlockers = gameState.gameObjectState.derivedState.permanentStates.values.view
      .filter(o => o.characteristics.types.contains(Type.Creature))
      .filter(o => o.controller == defendingPlayer)
      .filter(o => !o.gameObject.status.isTapped)
      .toSeq
    val restrictions = gameState.gameObjectState.activeContinuousEffects.ofType[BlockerRestriction].toSeq
    allPossibleBlockers.map(blockerState => {
      blockerState.gameObject.objectId -> attackerStates.filter(attackerState => !restrictions.exists(_.preventsBlock(attackerState, blockerState))).map(_.gameObject.objectId)
    }).filter(_._2.nonEmpty).toMap
  }
  def getBlockDeclarations(gameState: GameState): Seq[BlockDeclaration] = {
    gameState.eventsThisTurn
      .getDecision[DeclaredBlockers].toSeq
      .flatMap(_.blockDeclarations)
  }
  def isBlocking(objectId: ObjectId, gameState: GameState): Boolean = {
    def wasDeclaredBlocker = getBlockDeclarations(gameState).exists(_.blocker == objectId)
    wasDeclaredBlocker && !TurnPhase.CombatPhase.hasFinished(gameState)
  }

  def getBlockerOrderings(gameState: GameState): Seq[BlockerOrdering] = {
    gameState.eventsThisTurn
      .getDecisions[BlockerOrdering]
      .toSeq
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
    getBlockerOrderings(gameState).find(_.attacker == attacker).map(_.blockersInOrder)
      .orElse(getDefaultBlockerOrdering(attacker, gameState))
  }

  def getOrderingOfAttackersForBlocker(blocker: ObjectId, gameState: GameState): Option[Seq[ObjectId]] = {
    getBlockDeclarations(gameState)
      .find(_.blocker == blocker)
      .filter(_ => isBlocking(blocker, gameState))
      .map(d => Seq(d.attacker).filter(DeclareAttackers.isAttacking(_, gameState)))
  }
}

case class BlockDeclaration(blocker: ObjectId, attacker: ObjectId)
case class DeclaredBlockers(blockDeclarations: Seq[BlockDeclaration])

case class DeclareBlockersChoice(
    playerToAct: PlayerId,
    attackers: Seq[ObjectId],
    possibleBlockers: Map[ObjectId, Seq[ObjectId]])
  extends TypedPlayerChoice[DeclaredBlockers]
{
  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[DeclaredBlockers] = {
    ParsingUtils.splitStringAsIds(serializedChosenOption)
      .map(_.grouped(2).toSeq)
      .filter(_.forall(_.length == 2))
      .map(_.map { case Seq(a, b) => BlockDeclaration(a, b)} )
      .filter(_.forall(isValidBlock))
      .map(DeclaredBlockers)
  }
  private def isValidBlock(blockDeclaration: BlockDeclaration): Boolean = {
    possibleBlockers.get(blockDeclaration.blocker).exists(_.contains(blockDeclaration.attacker))
  }
  override def handleDecision(chosenBlocks: DeclaredBlockers, currentGameState: GameState): InternalGameActionResult = {
    def getName(objectId: ObjectId): String = currentGameState.gameObjectState.derivedState.permanentStates(objectId).characteristics.name.get
    val assignments = chosenBlocks.blockDeclarations.groupBy(_.attacker)
      .map { case (id, details) => (getName(id), details.map(_.blocker).map(getName)) }
    (Seq(OrderBlockers(chosenBlocks.blockDeclarations)), LogEvent.DeclareBlockers(playerToAct, assignments))
  }
}

case class OrderBlockers(blockDeclarations: Seq[BlockDeclaration]) extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    blockDeclarations
      .groupBy(_.attacker)
      .filter(_._2.length > 1)
      .map { case (attacker, blockDeclarations) =>
        OrderBlockersChoice(currentGameState.activePlayer, attacker, blockDeclarations.map(_.blocker).toSet)
      }.toSeq
  }
}

case class BlockerOrdering(attacker: ObjectId, blockersInOrder: Seq[ObjectId])

case class OrderBlockersChoice(playerToAct: PlayerId, attacker: ObjectId, blockers: Set[ObjectId]) extends TypedPlayerChoice[BlockerOrdering] {
  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[BlockerOrdering] = {
    ParsingUtils.splitStringAsIds(serializedChosenOption).flatMap {
      case blockersInOrder if blockersInOrder.toSet == blockers =>
        Some(BlockerOrdering(attacker, blockersInOrder))
      case _ =>
        None
    }
  }

  override def handleDecision(chosenOption: BlockerOrdering, currentGameState: GameState): InternalGameActionResult = {
    def getName(objectId: ObjectId): String = currentGameState.gameObjectState.derivedState.permanentStates(objectId).characteristics.name.get
    LogEvent.OrderBlockers(
      playerToAct,
      getName(chosenOption.attacker),
      chosenOption.blockersInOrder.map(getName))
  }
}
