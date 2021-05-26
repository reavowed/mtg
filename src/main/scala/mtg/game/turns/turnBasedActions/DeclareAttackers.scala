package mtg.game.turns.turnBasedActions

import mtg._
import mtg.characteristics.types.Type
import mtg.events.TapObjectEvent
import mtg.game.PlayerIdentifier
import mtg.game.objects.ObjectId
import mtg.game.state.history.GameEvent.ResolvedEvent
import mtg.game.state.history.LogEvent
import mtg.game.state._

object DeclareAttackers extends InternalGameAction {
  def wasContinuouslyControlled(objectWithState: ObjectWithState, gameState: GameState): Boolean = {
    gameState.gameHistory.forCurrentTurn.exists(
      _.gameEvents.ofType[ResolvedEvent].forall(
        _.stateAfterwards.objectStates.get(objectWithState.gameObject.objectId)
          .exists(_.controller.contains(gameState.activePlayer))))
  }
  def getPossibleAttackers(gameState: GameState): Seq[ObjectId] = {
    gameState.gameObjectState.battlefield.view
      .map(o => gameState.derivedState.objectStates(o.objectId))
      .filter(o => o.characteristics.types.contains(Type.Creature))
      .filter(o => o.controller.contains(gameState.activePlayer))
      .filter(o => o.gameObject.permanentStatus.exists(!_.isTapped))
      .filter(o => wasContinuouslyControlled(o, gameState))
      .map(o => o.gameObject.objectId)
      // TODO: Either haste or continuously controlled since the turn began
      .toSeq
  }

  override def execute(currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    val possibleAttackers = getPossibleAttackers(currentGameState)
    if (possibleAttackers.nonEmpty)
      (Seq(DeclareAttackersChoice(currentGameState.activePlayer, getPossibleAttackers(currentGameState))), None)
    else
      (Nil, None)
  }
}

case class DeclaredAttacker(attacker: ObjectId, attackedPlayer: PlayerIdentifier)
case class DeclaredAttackers(attackers: Seq[DeclaredAttacker])

case class DeclareAttackersChoice(playerToAct: PlayerIdentifier, possibleAttackers: Seq[ObjectId]) extends TypedChoice[DeclaredAttackers] {
  override def parseOption(serializedChosenOption: String, currentGameState: GameState): Option[DeclaredAttackers] = {
    val defendingPlayer = currentGameState.playersInApnapOrder.filter(_ != currentGameState.activePlayer).single
    serializedChosenOption
      .split(" ").toSeq
      .filter(_.nonEmpty)
      .map(_.toIntOption.flatMap(i => possibleAttackers.find(_.sequentialId == i)).map(DeclaredAttacker(_, defendingPlayer)))
      .swap
      .map(DeclaredAttackers)
  }

  override def handleDecision(chosenOption: DeclaredAttackers, currentGameState: GameState): (Seq[GameAction], Option[LogEvent]) = {
    import chosenOption._
    if (attackers.nonEmpty) {
      (
        Seq(TapAttackers(attackers.map(_.attacker))),
        Some(LogEvent.DeclareAttackers(
          playerToAct,
          attackers.map(_.attacker.currentCharacteristics(currentGameState).name.getOrElse("<unnamed creature>"))
        ))
      )
    } else {
      (Nil, None)
    }
  }
}

case class TapAttackers(attackers: Seq[ObjectId]) extends GameObjectEvent {
  override def execute(currentGameState: GameState): GameObjectEventResult = {
    attackers.map(TapObjectEvent)
  }
}
