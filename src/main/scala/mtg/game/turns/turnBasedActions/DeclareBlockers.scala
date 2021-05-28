package mtg.game.turns.turnBasedActions

import mtg.characteristics.types.Type
import mtg.game.PlayerIdentifier
import mtg.game.objects.ObjectId
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, InternalGameActionResult, TypedPlayerChoice}
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
  private def getPossibleBlockers(defendingPlayer: PlayerIdentifier, gameState: GameState): Seq[ObjectId] = {
    gameState.gameObjectState.battlefield.view
      .map(o => gameState.derivedState.objectStates(o.objectId))
      .filter(o => o.characteristics.types.contains(Type.Creature))
      .filter(o => o.controller.contains(defendingPlayer))
      .filter(o => o.gameObject.permanentStatus.exists(!_.isTapped))
      .map(o => o.gameObject.objectId)
      // TODO: Either haste or continuously controlled since the turn began
      .toSeq
  }
  def getBlockDeclarations(gameState: GameState): Seq[BlockDeclaration] = {
    gameState.gameHistory.forCurrentTurn.view
      .flatMap(_.gameEvents.getDecision[DeclaredBlockers])
      .flatMap(_.blockDeclarations)
      .toSeq
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
    playerToAct: PlayerIdentifier,
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
    def getName(objectId: ObjectId): String = currentGameState.derivedState.objectStates(objectId).characteristics.name
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

case class OrderBlockersChoice(playerToAct: PlayerIdentifier, attacker: ObjectId, blockers: Set[ObjectId]) extends TypedPlayerChoice[BlockerOrdering] {
  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[BlockerOrdering] = {
    ParsingUtils.splitStringAsIds(serializedChosenOption).flatMap {
      case blockersInOrder if blockersInOrder.toSet == blockers =>
        Some(BlockerOrdering(attacker, blockersInOrder))
      case _ =>
        None
    }
  }

  override def handleDecision(chosenOption: BlockerOrdering, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    def getName(objectId: ObjectId): String = currentGameState.derivedState.objectStates(objectId).characteristics.name
    (Nil, Some(LogEvent.OrderBlockers(
      playerToAct,
      getName(chosenOption.attacker),
      chosenOption.blockersInOrder.map(getName))))
  }
}
