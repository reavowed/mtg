package mtg.game.turns.turnBasedActions

import mtg.characteristics.types.Type
import mtg.game.{ObjectId, PlayerId}
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, InternalGameActionResult, TypedPlayerChoice}
import mtg.game.turns.TurnPhase
import mtg.utils.ParsingUtils

object DeclareBlockers extends InternalGameAction {
  override def execute(currentGameState: GameState): InternalGameActionResult = {
    val defendingPlayer = DeclareAttackers.getDefendingPlayer(currentGameState)
    val attackers = DeclareAttackers.getAttackDeclarations(currentGameState).map(_.attacker)
    val possibleBlockers = getPossibleBlockers(defendingPlayer, currentGameState)
    if (attackers.nonEmpty && possibleBlockers.nonEmpty)
      DeclareBlockersChoice(
        defendingPlayer,
        attackers,
        possibleBlockers)
    else
      ()
  }
  private def getPossibleBlockers(defendingPlayer: PlayerId, gameState: GameState): Seq[ObjectId] = {
    gameState.gameObjectState.derivedState.permanentStates.values.view
      .filter(o => o.characteristics.types.contains(Type.Creature))
      .filter(o => o.controller == defendingPlayer)
      .filter(o => !o.gameObject.status.isTapped)
      .map(o => o.gameObject.objectId)
      .toSeq
  }
  def getBlockDeclarations(gameState: GameState): Seq[BlockDeclaration] = {
    gameState.gameHistory.forCurrentTurn.view
      .flatMap(_.gameEvents.getDecision[DeclaredBlockers])
      .flatMap(_.blockDeclarations)
      .toSeq
  }
  def isBlocking(objectId: ObjectId, gameState: GameState): Boolean = {
    def wasDeclaredBlocker = getBlockDeclarations(gameState).exists(_.blocker == objectId)
    wasDeclaredBlocker && !TurnPhase.CombatPhase.hasFinished(gameState)
  }

  def getBlockerOrderings(gameState: GameState): Seq[BlockerOrdering] = {
    gameState.gameHistory.forCurrentTurn.view
      .flatMap(_.gameEvents.getDecisions[BlockerOrdering])
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
  def getBlockerOrdering(attacker: ObjectId, gameState: GameState): Option[Seq[ObjectId]] = {
    getBlockerOrderings(gameState).find(_.attacker == attacker).map(_.blockersInOrder)
      .orElse(getDefaultBlockerOrdering(attacker, gameState))
  }
}

case class BlockDeclaration(blocker: ObjectId, attacker: ObjectId)
case class DeclaredBlockers(blockDeclarations: Seq[BlockDeclaration])

case class DeclareBlockersChoice(
    playerToAct: PlayerId,
    attackers: Seq[ObjectId],
    possibleBlockers: Seq[ObjectId])
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
  private def isValidBlock(blockingCreatureDetails: BlockDeclaration): Boolean = {
    possibleBlockers.contains(blockingCreatureDetails.blocker) && attackers.contains(blockingCreatureDetails.attacker)
  }
  override def handleDecision(chosenBlocks: DeclaredBlockers, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    def getName(objectId: ObjectId): String = currentGameState.gameObjectState.derivedState.permanentStates(objectId).characteristics.name
    val assignments = chosenBlocks.blockDeclarations.groupBy(_.attacker)
      .map { case (id, details) => (getName(id), details.map(_.blocker).map(getName)) }
    (Seq(OrderBlockers(chosenBlocks.blockDeclarations)), Some(LogEvent.DeclareBlockers(playerToAct, assignments)))
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

  override def handleDecision(chosenOption: BlockerOrdering, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    def getName(objectId: ObjectId): String = currentGameState.gameObjectState.derivedState.permanentStates(objectId).characteristics.name
    (Nil, Some(LogEvent.OrderBlockers(
      playerToAct,
      getName(chosenOption.attacker),
      chosenOption.blockersInOrder.map(getName))))
  }
}
