package mtg.game.turns.turnBasedActions

import mtg.characteristics.types.Type
import mtg.game.PlayerIdentifier
import mtg.game.objects.ObjectId
import mtg.game.state.history.LogEvent
import mtg.game.state.{GameAction, GameState, InternalGameAction, TypedChoice}

object DeclareBlockers extends InternalGameAction {
  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val defendingPlayer = DeclareAttackers.getDefendingPlayer(currentGameState)
    val attackers = DeclareAttackers.getAttackingCreatures(currentGameState).map(_.attacker)
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
}

case class BlockingCreatureDetails(blocker: ObjectId, blockedCreature: ObjectId)
case class DeclaredBlockers(attackers: Seq[BlockingCreatureDetails])

case class DeclareBlockersChoice(
    playerToAct: PlayerIdentifier,
    attackers: Seq[ObjectId],
    possibleBlockers: Seq[ObjectId])
  extends TypedChoice[DeclaredBlockers]
{
  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[DeclaredBlockers] = ???
  override def handleDecision(chosenOption: DeclaredBlockers, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = ???
}
