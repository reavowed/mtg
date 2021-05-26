package mtg.game.turns.turnBasedActions

import mtg._
import mtg.characteristics.types.Type
import mtg.game.PlayerIdentifier
import mtg.game.objects.ObjectId
import mtg.game.state.history.GameEvent.Decision
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, TypedChoice}
import mtg.utils.ParsingUtils

object DeclareBlockers extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val defendingPlayer = DeclareAttackers.getDefendingPlayer(currentGameState)
    val attackers = DeclareAttackers.getAttackDeclarations(currentGameState).map(_.attacker)
    val possibleBlockers = getPossibleBlockers(defendingPlayer, currentGameState)
    val choice = DeclareBlockersChoice(
      defendingPlayer,
      attackers,
      possibleBlockers)
    (Seq(choice).filter(_.attackers.nonEmpty).filter(_.possibleBlockers.nonEmpty), None)
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
    gameState.gameHistory.forCurrentTurn
      .flatMap(
        _.gameEvents.view.ofType[Decision]
          .map(_.chosenOption)
          .mapFind(_.asOptionalInstanceOf[DeclaredBlockers]))
      .toSeq
      .flatMap(_.blockingDetails)
  }
  def getBlockDeclarationsForAttacker(attackerId: ObjectId, allBlockDeclarations: Seq[BlockDeclaration]): Seq[BlockDeclaration] = {
    allBlockDeclarations.filter(_.blockedCreature == attackerId)
  }
}

case class BlockDeclaration(blocker: ObjectId, blockedCreature: ObjectId)
case class DeclaredBlockers(blockingDetails: Seq[BlockDeclaration])

case class DeclareBlockersChoice(
    playerToAct: PlayerIdentifier,
    attackers: Seq[ObjectId],
    possibleBlockers: Seq[ObjectId])
  extends TypedChoice[DeclaredBlockers]
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
    possibleBlockers.contains(blockingCreatureDetails.blocker) && attackers.contains(blockingCreatureDetails.blockedCreature)
  }
  override def handleDecision(chosenOption: DeclaredBlockers, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    def getName(objectId: ObjectId): String = currentGameState.derivedState.objectStates(objectId).characteristics.name.getOrElse("<unnamed creature>")
    val assignments = chosenOption.blockingDetails.groupBy(_.blockedCreature)
      .map { case (id, details) => (getName(id), details.map(_.blocker).map(getName)) }
    (Nil, Some(LogEvent.DeclareBlockers(playerToAct, assignments)))
  }
}
